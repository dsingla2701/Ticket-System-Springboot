package com.ticketsystem.repository;

import com.ticketsystem.entity.Ticket;
import com.ticketsystem.entity.TicketRating;
import com.ticketsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRatingRepository extends JpaRepository<TicketRating, UUID> {

    // Find rating by ticket
    Optional<TicketRating> findByTicket(Ticket ticket);

    // Find ratings by user
    Page<TicketRating> findByRatedBy(User ratedBy, Pageable pageable);
    
    List<TicketRating> findByRatedByOrderByCreatedAtDesc(User ratedBy);

    // Find ratings by rating value
    List<TicketRating> findByRating(Integer rating);
    
    Page<TicketRating> findByRating(Integer rating, Pageable pageable);

    // Find ratings in range
    @Query("SELECT r FROM TicketRating r WHERE r.rating BETWEEN :minRating AND :maxRating")
    List<TicketRating> findByRatingBetween(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating);

    // Find positive ratings (4-5 stars)
    @Query("SELECT r FROM TicketRating r WHERE r.rating >= 4")
    List<TicketRating> findPositiveRatings();

    // Find negative ratings (1-2 stars)
    @Query("SELECT r FROM TicketRating r WHERE r.rating <= 2")
    List<TicketRating> findNegativeRatings();

    // Find ratings with feedback
    @Query("SELECT r FROM TicketRating r WHERE r.feedback IS NOT NULL AND r.feedback != ''")
    List<TicketRating> findRatingsWithFeedback();

    // Find ratings without feedback
    @Query("SELECT r FROM TicketRating r WHERE r.feedback IS NULL OR r.feedback = ''")
    List<TicketRating> findRatingsWithoutFeedback();

    // Search ratings by feedback content
    @Query("SELECT r FROM TicketRating r WHERE " +
           "r.feedback IS NOT NULL AND " +
           "LOWER(r.feedback) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<TicketRating> searchRatingsByFeedback(@Param("search") String search, Pageable pageable);

    // Find ratings in date range
    @Query("SELECT r FROM TicketRating r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    Page<TicketRating> findRatingsCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               Pageable pageable);

    // Find ratings with filters
    @Query("SELECT r FROM TicketRating r WHERE " +
           "(:rating IS NULL OR r.rating = :rating) AND " +
           "(:ratedBy IS NULL OR r.ratedBy = :ratedBy) AND " +
           "(:hasFeedback IS NULL OR " +
           "(:hasFeedback = true AND r.feedback IS NOT NULL AND r.feedback != '') OR " +
           "(:hasFeedback = false AND (r.feedback IS NULL OR r.feedback = '')))")
    Page<TicketRating> findRatingsWithFilters(@Param("rating") Integer rating,
                                            @Param("ratedBy") User ratedBy,
                                            @Param("hasFeedback") Boolean hasFeedback,
                                            Pageable pageable);

    // Statistics queries
    @Query("SELECT AVG(r.rating) FROM TicketRating r")
    Double getAverageRating();

    @Query("SELECT COUNT(r) FROM TicketRating r WHERE r.rating = :rating")
    long countByRating(@Param("rating") Integer rating);

    @Query("SELECT r.rating, COUNT(r) FROM TicketRating r GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistribution();

    // Find recent ratings
    @Query("SELECT r FROM TicketRating r WHERE r.createdAt >= :since ORDER BY r.createdAt DESC")
    List<TicketRating> findRecentRatings(@Param("since") LocalDateTime since);

    // Check if user has rated a ticket
    boolean existsByTicketAndRatedBy(Ticket ticket, User ratedBy);

    // Find ratings for tickets assigned to a specific agent
    @Query("SELECT r FROM TicketRating r WHERE r.ticket.assignedTo = :agent")
    List<TicketRating> findRatingsForAgent(@Param("agent") User agent);

    // Get average rating for tickets assigned to a specific agent
    @Query("SELECT AVG(r.rating) FROM TicketRating r WHERE r.ticket.assignedTo = :agent")
    Double getAverageRatingForAgent(@Param("agent") User agent);

    // Find top rated agents
    @Query("SELECT r.ticket.assignedTo, AVG(r.rating) FROM TicketRating r " +
           "WHERE r.ticket.assignedTo IS NOT NULL " +
           "GROUP BY r.ticket.assignedTo " +
           "ORDER BY AVG(r.rating) DESC")
    List<Object[]> getTopRatedAgents();

    // Count ratings created since date
    @Query("SELECT COUNT(r) FROM TicketRating r WHERE r.createdAt >= :since")
    long countRatingsCreatedSince(@Param("since") LocalDateTime since);

    // Find ratings by ticket creator
    @Query("SELECT r FROM TicketRating r WHERE r.ticket.createdBy = :creator")
    List<TicketRating> findRatingsByTicketCreator(@Param("creator") User creator);

    // Get satisfaction rate (percentage of ratings 4 or above)
    @Query("SELECT " +
           "(CAST(COUNT(CASE WHEN r.rating >= 4 THEN 1 END) AS DOUBLE) / COUNT(r)) * 100 " +
           "FROM TicketRating r")
    Double getSatisfactionRate();
}
