package com.ticketsystem.service;

import com.ticketsystem.dto.CreateUserRequest;
import com.ticketsystem.dto.SystemStatsDto;
import com.ticketsystem.dto.UpdateUserRequest;
import com.ticketsystem.entity.*;
import com.ticketsystem.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private TicketRatingRepository ticketRatingRepository;

    // User Management
    public User createUser(CreateUserRequest request) {
        return userService.createUser(
            request.getEmail(),
            request.getPassword(),
            request.getFirstName(),
            request.getLastName(),
            request.getRole()
        );
    }

    public User updateUser(UUID userId, UpdateUserRequest request) {
        User user = userService.getUserById(userId);
        
        if (request.getEmail() != null) {
            user = userService.updateUser(userId, 
                request.getFirstName() != null ? request.getFirstName() : user.getFirstName(),
                request.getLastName() != null ? request.getLastName() : user.getLastName(),
                request.getEmail());
        } else if (request.getFirstName() != null || request.getLastName() != null) {
            user = userService.updateUser(userId,
                request.getFirstName() != null ? request.getFirstName() : user.getFirstName(),
                request.getLastName() != null ? request.getLastName() : user.getLastName(),
                user.getEmail());
        }

        if (request.getRole() != null && !request.getRole().equals(user.getRole())) {
            user = userService.updateUserRole(userId, request.getRole());
        }

        if (request.getIsActive() != null && !request.getIsActive().equals(user.getIsActive())) {
            user = request.getIsActive() ? userService.activateUser(userId) : userService.deactivateUser(userId);
        }

        logger.info("Admin updated user: {}", userId);
        return user;
    }

    public void deleteUser(UUID userId) {
        userService.deleteUser(userId);
        logger.info("Admin deleted user: {}", userId);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    public Page<User> searchUsers(String search, Pageable pageable) {
        return userService.searchUsers(search, pageable);
    }

    public Page<User> getUsersWithFilters(UserRole role, Boolean isActive, String search, Pageable pageable) {
        return userService.getUsersWithFilters(role, isActive, search, pageable);
    }

    // System Statistics
    public SystemStatsDto getSystemStats() {
        SystemStatsDto stats = new SystemStatsDto();
        
        // User Statistics
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.findByIsActiveTrue(Pageable.unpaged()).getTotalElements());
        stats.setAdminUsers(userService.getUserCountByRole(UserRole.ADMIN));
        stats.setSupportAgents(userService.getUserCountByRole(UserRole.SUPPORT_AGENT));
        stats.setRegularUsers(userService.getUserCountByRole(UserRole.USER));
        
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekAgo = today.minusDays(7);
        LocalDateTime monthAgo = today.minusDays(30);
        
        stats.setNewUsersToday(getUsersCreatedSince(today));
        stats.setNewUsersThisWeek(getUsersCreatedSince(weekAgo));
        stats.setNewUsersThisMonth(getUsersCreatedSince(monthAgo));

        // Ticket Statistics
        stats.setTotalTickets(ticketRepository.count());
        stats.setOpenTickets(ticketService.getTicketCountByStatus(TicketStatus.OPEN));
        stats.setInProgressTickets(ticketService.getTicketCountByStatus(TicketStatus.IN_PROGRESS));
        stats.setResolvedTickets(ticketService.getTicketCountByStatus(TicketStatus.RESOLVED));
        stats.setClosedTickets(ticketService.getTicketCountByStatus(TicketStatus.CLOSED));
        stats.setUnassignedTickets(ticketService.getUnassignedTicketCount());
        stats.setOverdueTickets((long) ticketService.getOverdueTickets(3).size()); // 3 days overdue

        stats.setNewTicketsToday(getTicketsCreatedSince(today));
        stats.setNewTicketsThisWeek(getTicketsCreatedSince(weekAgo));
        stats.setNewTicketsThisMonth(getTicketsCreatedSince(monthAgo));

        // Priority Statistics
        stats.setLowPriorityTickets(ticketService.getTicketCountByPriority(TicketPriority.LOW));
        stats.setMediumPriorityTickets(ticketService.getTicketCountByPriority(TicketPriority.MEDIUM));
        stats.setHighPriorityTickets(ticketService.getTicketCountByPriority(TicketPriority.HIGH));
        stats.setUrgentPriorityTickets(ticketService.getTicketCountByPriority(TicketPriority.URGENT));

        // Performance Statistics
        stats.setAverageResolutionTimeHours(ticketService.getAverageResolutionTimeInHours());
        stats.setCustomerSatisfactionRate(getCustomerSatisfactionRate());
        stats.setTotalComments(commentRepository.count());
        stats.setTotalAttachments(attachmentRepository.count());
        stats.setTotalStorageUsedMB(getTotalStorageUsedMB());

        // Recent Activity
        stats.setLastTicketCreated(getLastTicketCreatedTime());
        stats.setLastTicketResolved(getLastTicketResolvedTime());
        stats.setLastUserRegistered(getLastUserRegisteredTime());

        // Distribution Maps
        stats.setTicketsByStatus(getTicketsByStatusMap());
        stats.setTicketsByPriority(getTicketsByPriorityMap());
        stats.setUsersByRole(getUsersByRoleMap());
        stats.setTicketsCreatedByDay(getTicketsCreatedByDayMap());
        stats.setTicketsResolvedByDay(getTicketsResolvedByDayMap());

        return stats;
    }

    // Bulk Operations
    public void bulkUpdateUserStatus(List<UUID> userIds, boolean isActive) {
        for (UUID userId : userIds) {
            try {
                if (isActive) {
                    userService.activateUser(userId);
                } else {
                    userService.deactivateUser(userId);
                }
            } catch (Exception e) {
                logger.error("Failed to update status for user {}: {}", userId, e.getMessage());
            }
        }
        logger.info("Bulk updated status for {} users to {}", userIds.size(), isActive ? "active" : "inactive");
    }

    public void bulkUpdateUserRole(List<UUID> userIds, UserRole role) {
        for (UUID userId : userIds) {
            try {
                userService.updateUserRole(userId, role);
            } catch (Exception e) {
                logger.error("Failed to update role for user {}: {}", userId, e.getMessage());
            }
        }
        logger.info("Bulk updated role for {} users to {}", userIds.size(), role);
    }

    public void bulkDeleteUsers(List<UUID> userIds) {
        for (UUID userId : userIds) {
            try {
                userService.deleteUser(userId);
            } catch (Exception e) {
                logger.error("Failed to delete user {}: {}", userId, e.getMessage());
            }
        }
        logger.info("Bulk deleted {} users", userIds.size());
    }

    // Helper methods for statistics
    private Long getUsersCreatedSince(LocalDateTime since) {
        return userRepository.findAll().stream()
            .filter(user -> user.getCreatedAt().isAfter(since))
            .count();
    }

    private Long getTicketsCreatedSince(LocalDateTime since) {
        return ticketRepository.findRecentTickets(since).size();
    }

    private Double getCustomerSatisfactionRate() {
        return ticketRatingRepository.getSatisfactionRate();
    }

    private Long getTotalStorageUsedMB() {
        Long totalBytes = attachmentRepository.getTotalStorageUsed();
        return totalBytes != null ? totalBytes / (1024 * 1024) : 0L;
    }

    private LocalDateTime getLastTicketCreatedTime() {
        return ticketRepository.findAll().stream()
            .map(Ticket::getCreatedAt)
            .max(LocalDateTime::compareTo)
            .orElse(null);
    }

    private LocalDateTime getLastTicketResolvedTime() {
        return ticketRepository.findAll().stream()
            .filter(ticket -> ticket.getResolvedAt() != null)
            .map(Ticket::getResolvedAt)
            .max(LocalDateTime::compareTo)
            .orElse(null);
    }

    private LocalDateTime getLastUserRegisteredTime() {
        return userRepository.findAll().stream()
            .map(User::getCreatedAt)
            .max(LocalDateTime::compareTo)
            .orElse(null);
    }

    private Map<String, Long> getTicketsByStatusMap() {
        Map<String, Long> map = new HashMap<>();
        for (TicketStatus status : TicketStatus.values()) {
            map.put(status.name(), ticketService.getTicketCountByStatus(status));
        }
        return map;
    }

    private Map<String, Long> getTicketsByPriorityMap() {
        Map<String, Long> map = new HashMap<>();
        for (TicketPriority priority : TicketPriority.values()) {
            map.put(priority.name(), ticketService.getTicketCountByPriority(priority));
        }
        return map;
    }

    private Map<String, Long> getUsersByRoleMap() {
        Map<String, Long> map = new HashMap<>();
        for (UserRole role : UserRole.values()) {
            map.put(role.name(), userService.getUserCountByRole(role));
        }
        return map;
    }

    private Map<String, Long> getTicketsCreatedByDayMap() {
        Map<String, Long> map = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDateTime day = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime nextDay = day.plusDays(1);
            
            long count = ticketRepository.findTicketsCreatedBetween(day, nextDay, Pageable.unpaged()).getTotalElements();
            map.put(day.format(formatter), count);
        }
        return map;
    }

    private Map<String, Long> getTicketsResolvedByDayMap() {
        Map<String, Long> map = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDateTime day = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime nextDay = day.plusDays(1);
            
            long count = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getResolvedAt() != null)
                .filter(ticket -> ticket.getResolvedAt().isAfter(day) && ticket.getResolvedAt().isBefore(nextDay))
                .count();
            
            map.put(day.format(formatter), count);
        }
        return map;
    }
}
