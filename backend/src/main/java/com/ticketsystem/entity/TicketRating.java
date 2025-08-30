package com.ticketsystem.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ticket_id", "rated_by_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class TicketRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_by_id", nullable = false)
    private User ratedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public TicketRating() {}

    public TicketRating(Integer rating, String feedback, Ticket ticket, User ratedBy) {
        this.rating = rating;
        this.feedback = feedback;
        this.ticket = ticket;
        this.ratedBy = ratedBy;
    }

    // Business methods
    public boolean canBeEditedBy(User user) {
        return user.equals(this.ratedBy);
    }

    public boolean canBeViewedBy(User user) {
        return user.getRole().hasAdminPrivileges() || 
               user.getRole().hasSupportPrivileges() || 
               user.equals(this.ratedBy) ||
               user.equals(this.ticket.getCreatedBy());
    }

    public boolean isPositive() {
        return rating != null && rating >= 4;
    }

    public boolean isNegative() {
        return rating != null && rating <= 2;
    }

    public boolean isNeutral() {
        return rating != null && rating == 3;
    }

    public String getRatingDescription() {
        if (rating == null) return "No rating";
        
        switch (rating) {
            case 1: return "Very Poor";
            case 2: return "Poor";
            case 3: return "Average";
            case 4: return "Good";
            case 5: return "Excellent";
            default: return "Unknown";
        }
    }

    public String getStarDisplay() {
        if (rating == null) return "☆☆☆☆☆";
        
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stars.append(i <= rating ? "★" : "☆");
        }
        return stars.toString();
    }

    // Validation
    @PrePersist
    @PreUpdate
    private void validateRating() {
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(User ratedBy) {
        this.ratedBy = ratedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
