package com.ticketsystem.service;

import com.ticketsystem.dto.CreateCommentRequest;
import com.ticketsystem.entity.Comment;
import com.ticketsystem.entity.Ticket;
import com.ticketsystem.entity.User;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.exception.UnauthorizedOperationException;
import com.ticketsystem.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TicketService ticketService;

    public Comment addComment(UUID ticketId, CreateCommentRequest request, User author) {
        Ticket ticket = ticketService.getTicketByIdWithAccess(ticketId, author);
        
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTicket(ticket);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);
        logger.info("Added comment to ticket {} by user {}", ticketId, author.getEmail());
        return savedComment;
    }

    public Comment addSystemComment(Ticket ticket, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setTicket(ticket);
        comment.setAuthor(ticket.getCreatedBy()); // Use ticket creator as system comment author
        
        Comment savedComment = commentRepository.save(comment);
        logger.debug("Added system comment to ticket {}", ticket.getId());
        return savedComment;
    }

    public Comment getCommentById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
    }

    public Comment getCommentByIdWithAccess(UUID id, User user) {
        Comment comment = getCommentById(id);
        
        if (!canUserAccessComment(comment, user)) {
            throw new UnauthorizedOperationException("You don't have permission to access this comment");
        }
        
        return comment;
    }

    public List<Comment> getCommentsByTicket(UUID ticketId, User user) {
        Ticket ticket = ticketService.getTicketByIdWithAccess(ticketId, user);
        return commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    public Page<Comment> getCommentsByTicket(UUID ticketId, User user, Pageable pageable) {
        Ticket ticket = ticketService.getTicketByIdWithAccess(ticketId, user);
        return commentRepository.findByTicket(ticket, pageable);
    }

    public Page<Comment> getCommentsByAuthor(User author, Pageable pageable) {
        return commentRepository.findByAuthor(author, pageable);
    }

    public Page<Comment> searchComments(String search, Pageable pageable) {
        return commentRepository.searchComments(search, pageable);
    }

    public Comment updateComment(UUID id, String newContent, User updatedBy) {
        Comment comment = getCommentById(id);
        
        if (!comment.canBeEditedBy(updatedBy)) {
            throw new UnauthorizedOperationException("You don't have permission to edit this comment");
        }

        comment.setContent(newContent);
        Comment savedComment = commentRepository.save(comment);
        
        logger.info("Updated comment {} by user {}", id, updatedBy.getEmail());
        return savedComment;
    }

    public void deleteComment(UUID id, User deletedBy) {
        Comment comment = getCommentById(id);
        
        if (!comment.canBeDeletedBy(deletedBy)) {
            throw new UnauthorizedOperationException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
        logger.info("Deleted comment {} by user {}", id, deletedBy.getEmail());
    }

    public List<Comment> getRecentCommentsByTicket(UUID ticketId, User user, int hours) {
        Ticket ticket = ticketService.getTicketByIdWithAccess(ticketId, user);
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return commentRepository.findRecentCommentsByTicket(ticket, since);
    }

    public long getCommentCountByTicket(UUID ticketId, User user) {
        Ticket ticket = ticketService.getTicketByIdWithAccess(ticketId, user);
        return commentRepository.countByTicket(ticket);
    }

    public long getCommentCountByAuthor(User author) {
        return commentRepository.countByAuthor(author);
    }

    // Helper methods
    private boolean canUserAccessComment(Comment comment, User user) {
        return user.getRole().hasAdminPrivileges() || 
               user.getRole().hasSupportPrivileges() || 
               user.equals(comment.getTicket().getCreatedBy()) ||
               user.equals(comment.getTicket().getAssignedTo()) ||
               user.equals(comment.getAuthor());
    }
}
