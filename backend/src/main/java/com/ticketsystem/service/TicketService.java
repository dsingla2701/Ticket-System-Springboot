package com.ticketsystem.service;

import com.ticketsystem.dto.CreateTicketRequest;
import com.ticketsystem.dto.UpdateTicketRequest;
import com.ticketsystem.entity.*;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.exception.UnauthorizedOperationException;
import com.ticketsystem.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    public Ticket createTicket(CreateTicketRequest request, User createdBy) {
        Ticket ticket = new Ticket();
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setCreatedBy(createdBy);
        ticket.setStatus(TicketStatus.OPEN);

        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Add system comment for ticket creation
        commentService.addSystemComment(savedTicket, 
            String.format("[SYSTEM] Ticket created by %s", createdBy.getFullName()));
        
        logger.info("Created new ticket with ID: {} by user: {}", savedTicket.getId(), createdBy.getEmail());
        return savedTicket;
    }

    public Ticket getTicketById(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    public Ticket getTicketByIdWithAccess(UUID id, User user) {
        Ticket ticket = getTicketById(id);
        
        if (!canUserAccessTicket(ticket, user)) {
            throw new UnauthorizedOperationException("You don't have permission to access this ticket");
        }
        
        return ticket;
    }

    public Page<Ticket> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    public Page<Ticket> getTicketsForUser(User user, Pageable pageable) {
        if (user.getRole().hasSupportPrivileges()) {
            return ticketRepository.findAll(pageable);
        } else {
            return ticketRepository.findByCreatedBy(user, pageable);
        }
    }

    public Page<Ticket> getAssignedTickets(User agent, Pageable pageable) {
        return ticketRepository.findByAssignedTo(agent, pageable);
    }

    public Page<Ticket> getUnassignedTickets(Pageable pageable) {
        return ticketRepository.findByAssignedToIsNull(pageable);
    }

    public Page<Ticket> searchTickets(String search, Pageable pageable) {
        return ticketRepository.searchTickets(search, pageable);
    }

    public Page<Ticket> getTicketsWithFilters(TicketStatus status, TicketPriority priority,
                                            User assignedTo, User createdBy, String search,
                                            Pageable pageable) {
        return ticketRepository.findTicketsWithFilters(status, priority, assignedTo, createdBy, search, pageable);
    }

    public Ticket updateTicket(UUID id, UpdateTicketRequest request, User updatedBy) {
        Ticket ticket = getTicketById(id);
        
        if (!ticket.canBeEditedBy(updatedBy)) {
            throw new UnauthorizedOperationException("You don't have permission to edit this ticket");
        }

        boolean hasChanges = false;
        StringBuilder changeLog = new StringBuilder("[SYSTEM] Ticket updated by " + updatedBy.getFullName() + ": ");

        if (request.getSubject() != null && !request.getSubject().equals(ticket.getSubject())) {
            ticket.setSubject(request.getSubject());
            changeLog.append("Subject changed; ");
            hasChanges = true;
        }

        if (request.getDescription() != null && !request.getDescription().equals(ticket.getDescription())) {
            ticket.setDescription(request.getDescription());
            changeLog.append("Description updated; ");
            hasChanges = true;
        }

        if (request.getPriority() != null && !request.getPriority().equals(ticket.getPriority())) {
            TicketPriority oldPriority = ticket.getPriority();
            ticket.setPriority(request.getPriority());
            changeLog.append(String.format("Priority changed from %s to %s; ", 
                oldPriority.getDisplayName(), request.getPriority().getDisplayName()));
            hasChanges = true;
        }

        if (request.getStatus() != null && !request.getStatus().equals(ticket.getStatus())) {
            if (!ticket.canChangeStatus(updatedBy)) {
                throw new UnauthorizedOperationException("You don't have permission to change ticket status");
            }
            updateTicketStatus(ticket, request.getStatus(), updatedBy, changeLog);
            hasChanges = true;
        }

        if (request.getAssignedToId() != null) {
            if (!ticket.canBeAssignedBy(updatedBy)) {
                throw new UnauthorizedOperationException("You don't have permission to assign tickets");
            }
            assignTicket(ticket, request.getAssignedToId(), updatedBy, changeLog);
            hasChanges = true;
        }

        if (hasChanges) {
            Ticket savedTicket = ticketRepository.save(ticket);
            commentService.addSystemComment(savedTicket, changeLog.toString());
            logger.info("Updated ticket with ID: {} by user: {}", id, updatedBy.getEmail());
            return savedTicket;
        }

        return ticket;
    }

    public Ticket assignTicket(UUID ticketId, UUID agentId, User assignedBy) {
        Ticket ticket = getTicketById(ticketId);
        
        if (!ticket.canBeAssignedBy(assignedBy)) {
            throw new UnauthorizedOperationException("You don't have permission to assign tickets");
        }

        User agent = userService.getUserById(agentId);
        if (!agent.getRole().hasSupportPrivileges()) {
            throw new IllegalArgumentException("User must be a support agent or admin to be assigned tickets");
        }

        StringBuilder changeLog = new StringBuilder();
        assignTicket(ticket, agentId, assignedBy, changeLog);
        
        Ticket savedTicket = ticketRepository.save(ticket);
        commentService.addSystemComment(savedTicket, changeLog.toString());
        
        logger.info("Assigned ticket {} to agent {} by {}", ticketId, agentId, assignedBy.getEmail());
        return savedTicket;
    }

    public Ticket unassignTicket(UUID ticketId, User unassignedBy) {
        Ticket ticket = getTicketById(ticketId);
        
        if (!ticket.canBeAssignedBy(unassignedBy)) {
            throw new UnauthorizedOperationException("You don't have permission to unassign tickets");
        }

        if (ticket.getAssignedTo() == null) {
            throw new IllegalStateException("Ticket is not assigned to anyone");
        }

        String previousAgent = ticket.getAssignedTo().getFullName();
        ticket.unassign();
        
        Ticket savedTicket = ticketRepository.save(ticket);
        commentService.addSystemComment(savedTicket, 
            String.format("[SYSTEM] Ticket unassigned from %s by %s", previousAgent, unassignedBy.getFullName()));
        
        logger.info("Unassigned ticket {} by {}", ticketId, unassignedBy.getEmail());
        return savedTicket;
    }

    public Ticket updateTicketStatus(UUID ticketId, TicketStatus newStatus, User updatedBy) {
        Ticket ticket = getTicketById(ticketId);
        
        if (!ticket.canChangeStatus(updatedBy)) {
            throw new UnauthorizedOperationException("You don't have permission to change ticket status");
        }

        StringBuilder changeLog = new StringBuilder();
        updateTicketStatus(ticket, newStatus, updatedBy, changeLog);
        
        Ticket savedTicket = ticketRepository.save(ticket);
        commentService.addSystemComment(savedTicket, changeLog.toString());
        
        logger.info("Updated ticket {} status to {} by {}", ticketId, newStatus, updatedBy.getEmail());
        return savedTicket;
    }

    public void deleteTicket(UUID id, User deletedBy) {
        Ticket ticket = getTicketById(id);
        
        if (!deletedBy.getRole().hasAdminPrivileges()) {
            throw new UnauthorizedOperationException("Only administrators can delete tickets");
        }

        ticketRepository.delete(ticket);
        logger.info("Deleted ticket with ID: {} by admin: {}", id, deletedBy.getEmail());
    }

    // Helper methods
    private boolean canUserAccessTicket(Ticket ticket, User user) {
        return user.getRole().hasAdminPrivileges() || 
               user.getRole().hasSupportPrivileges() || 
               user.equals(ticket.getCreatedBy()) ||
               user.equals(ticket.getAssignedTo());
    }

    private void updateTicketStatus(Ticket ticket, TicketStatus newStatus, User updatedBy, StringBuilder changeLog) {
        TicketStatus oldStatus = ticket.getStatus();
        ticket.updateStatus(newStatus);
        changeLog.append(String.format("[SYSTEM] Status changed from %s to %s by %s; ", 
            oldStatus.getDisplayName(), newStatus.getDisplayName(), updatedBy.getFullName()));
    }

    private void assignTicket(Ticket ticket, UUID agentId, User assignedBy, StringBuilder changeLog) {
        User agent = userService.getUserById(agentId);
        
        if (ticket.getAssignedTo() != null) {
            changeLog.append(String.format("[SYSTEM] Ticket reassigned from %s to %s by %s; ", 
                ticket.getAssignedTo().getFullName(), agent.getFullName(), assignedBy.getFullName()));
        } else {
            changeLog.append(String.format("[SYSTEM] Ticket assigned to %s by %s; ", 
                agent.getFullName(), assignedBy.getFullName()));
        }
        
        ticket.assignTo(agent);
    }

    // Statistics methods
    public long getTicketCountByStatus(TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    public long getTicketCountByPriority(TicketPriority priority) {
        return ticketRepository.countByPriority(priority);
    }

    public long getAssignedTicketCount(User agent) {
        return ticketRepository.countByAssignedTo(agent);
    }

    public long getCreatedTicketCount(User user) {
        return ticketRepository.countByCreatedBy(user);
    }

    public long getUnassignedTicketCount() {
        return ticketRepository.countUnassignedTickets();
    }

    public List<Ticket> getOverdueTickets(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return ticketRepository.findOverdueTickets(cutoffDate);
    }

    public List<Ticket> getRecentTickets(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return ticketRepository.findRecentTickets(since);
    }

    public Double getAverageResolutionTimeInHours() {
        Double seconds = ticketRepository.getAverageResolutionTimeInSeconds();
        return seconds != null ? seconds / 3600.0 : null;
    }
}
