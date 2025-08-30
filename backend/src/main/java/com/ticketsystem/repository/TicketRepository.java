package com.ticketsystem.repository;

import com.ticketsystem.entity.Ticket;
import com.ticketsystem.entity.TicketPriority;
import com.ticketsystem.entity.TicketStatus;
import com.ticketsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    // Find tickets by creator
    Page<Ticket> findByCreatedBy(User createdBy, Pageable pageable);
    
    List<Ticket> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    // Find tickets by assignee
    Page<Ticket> findByAssignedTo(User assignedTo, Pageable pageable);
    
    List<Ticket> findByAssignedToOrderByPriorityDescCreatedAtDesc(User assignedTo);

    // Find tickets by status
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
    
    List<Ticket> findByStatusIn(List<TicketStatus> statuses);

    // Find tickets by priority
    Page<Ticket> findByPriority(TicketPriority priority, Pageable pageable);
    
    List<Ticket> findByPriorityOrderByCreatedAtDesc(TicketPriority priority);

    // Find unassigned tickets
    Page<Ticket> findByAssignedToIsNull(Pageable pageable);
    
    List<Ticket> findByAssignedToIsNullAndStatusOrderByPriorityDescCreatedAtDesc(TicketStatus status);

    // Complex queries with multiple filters
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:assignedTo IS NULL OR t.assignedTo = :assignedTo) AND " +
           "(:createdBy IS NULL OR t.createdBy = :createdBy) AND " +
           "(:search IS NULL OR " +
           "LOWER(t.subject) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Ticket> findTicketsWithFilters(@Param("status") TicketStatus status,
                                       @Param("priority") TicketPriority priority,
                                       @Param("assignedTo") User assignedTo,
                                       @Param("createdBy") User createdBy,
                                       @Param("search") String search,
                                       Pageable pageable);

    // Search tickets by text
    @Query("SELECT t FROM Ticket t WHERE " +
           "LOWER(t.subject) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Ticket> searchTickets(@Param("search") String search, Pageable pageable);

    // Find tickets created in date range
    @Query("SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    Page<Ticket> findTicketsCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);

    // Find tickets for user (created by or assigned to)
    @Query("SELECT t FROM Ticket t WHERE t.createdBy = :user OR t.assignedTo = :user")
    Page<Ticket> findTicketsForUser(@Param("user") User user, Pageable pageable);

    // Statistics queries
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(@Param("status") TicketStatus status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.priority = :priority")
    long countByPriority(@Param("priority") TicketPriority priority);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo = :user")
    long countByAssignedTo(@Param("user") User user);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdBy = :user")
    long countByCreatedBy(@Param("user") User user);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo IS NULL")
    long countUnassignedTickets();

    // Find overdue tickets (in progress for more than specified days)
    @Query("SELECT t FROM Ticket t WHERE t.status = 'IN_PROGRESS' AND t.updatedAt < :cutoffDate")
    List<Ticket> findOverdueTickets(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find tickets by multiple statuses
    @Query("SELECT t FROM Ticket t WHERE t.status IN :statuses")
    Page<Ticket> findByStatusIn(@Param("statuses") List<TicketStatus> statuses, Pageable pageable);

    // Find tickets by multiple priorities
    @Query("SELECT t FROM Ticket t WHERE t.priority IN :priorities")
    Page<Ticket> findByPriorityIn(@Param("priorities") List<TicketPriority> priorities, Pageable pageable);

    // Find recent tickets
    @Query("SELECT t FROM Ticket t WHERE t.createdAt >= :since ORDER BY t.createdAt DESC")
    List<Ticket> findRecentTickets(@Param("since") LocalDateTime since);

    // Find tickets requiring attention (high priority, unassigned, or overdue)
    @Query("SELECT t FROM Ticket t WHERE " +
           "(t.priority IN ('HIGH', 'URGENT')) OR " +
           "(t.assignedTo IS NULL AND t.status = 'OPEN') OR " +
           "(t.status = 'IN_PROGRESS' AND t.updatedAt < :overdueDate)")
    List<Ticket> findTicketsRequiringAttention(@Param("overdueDate") LocalDateTime overdueDate);

    // Average resolution time
    @Query("SELECT AVG(EXTRACT(EPOCH FROM (t.resolvedAt - t.createdAt))) FROM Ticket t WHERE t.resolvedAt IS NOT NULL")
    Double getAverageResolutionTimeInSeconds();

    // Find tickets with ratings
    @Query("SELECT t FROM Ticket t WHERE t.rating IS NOT NULL")
    Page<Ticket> findTicketsWithRatings(Pageable pageable);
}
