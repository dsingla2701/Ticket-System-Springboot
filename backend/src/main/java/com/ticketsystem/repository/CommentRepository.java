package com.ticketsystem.repository;

import com.ticketsystem.entity.Comment;
import com.ticketsystem.entity.Ticket;
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
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // Find comments by ticket
    List<Comment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
    
    Page<Comment> findByTicket(Ticket ticket, Pageable pageable);

    // Find comments by author
    Page<Comment> findByAuthor(User author, Pageable pageable);
    
    List<Comment> findByAuthorOrderByCreatedAtDesc(User author);

    // Find comments by ticket and author
    List<Comment> findByTicketAndAuthorOrderByCreatedAtAsc(Ticket ticket, User author);

    // Search comments by content
    @Query("SELECT c FROM Comment c WHERE " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Comment> searchComments(@Param("search") String search, Pageable pageable);

    // Find comments in date range
    @Query("SELECT c FROM Comment c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    Page<Comment> findCommentsCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);

    // Find recent comments for a ticket
    @Query("SELECT c FROM Comment c WHERE c.ticket = :ticket AND c.createdAt >= :since ORDER BY c.createdAt DESC")
    List<Comment> findRecentCommentsByTicket(@Param("ticket") Ticket ticket, @Param("since") LocalDateTime since);

    // Count comments by ticket
    long countByTicket(Ticket ticket);

    // Count comments by author
    long countByAuthor(User author);

    // Find latest comment for each ticket
    @Query("SELECT c FROM Comment c WHERE c.id IN (" +
           "SELECT MAX(c2.id) FROM Comment c2 GROUP BY c2.ticket)")
    List<Comment> findLatestCommentPerTicket();

    // Find comments with filters
    @Query("SELECT c FROM Comment c WHERE " +
           "(:ticket IS NULL OR c.ticket = :ticket) AND " +
           "(:author IS NULL OR c.author = :author) AND " +
           "(:search IS NULL OR LOWER(c.content) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Comment> findCommentsWithFilters(@Param("ticket") Ticket ticket,
                                        @Param("author") User author,
                                        @Param("search") String search,
                                        Pageable pageable);

    // Find system comments (if we implement them)
    @Query("SELECT c FROM Comment c WHERE c.content LIKE '[SYSTEM]%'")
    List<Comment> findSystemComments();

    // Find comments that mention a user (if we implement @mentions)
    @Query("SELECT c FROM Comment c WHERE c.content LIKE CONCAT('%@', :username, '%')")
    List<Comment> findCommentsMentioningUser(@Param("username") String username);

    // Statistics
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.createdAt >= :since")
    long countCommentsCreatedSince(@Param("since") LocalDateTime since);

    @Query("SELECT c.author, COUNT(c) FROM Comment c GROUP BY c.author ORDER BY COUNT(c) DESC")
    List<Object[]> findMostActiveCommenters();
}
