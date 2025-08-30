package com.ticketsystem.dto;

import com.ticketsystem.entity.TicketRating;

import java.time.LocalDateTime;
import java.util.UUID;

public class TicketRatingDto {

    private UUID id;
    private Integer rating;
    private String feedback;
    private UUID ticketId;
    private UserDto ratedBy;
    private LocalDateTime createdAt;
    private String ratingDescription;
    private String starDisplay;
    private boolean isPositive;
    private boolean isNegative;
    private boolean isNeutral;

    // Constructors
    public TicketRatingDto() {}

    public TicketRatingDto(UUID id, Integer rating, String feedback, UUID ticketId,
                          UserDto ratedBy, LocalDateTime createdAt) {
        this.id = id;
        this.rating = rating;
        this.feedback = feedback;
        this.ticketId = ticketId;
        this.ratedBy = ratedBy;
        this.createdAt = createdAt;
        
        // Set computed fields
        this.ratingDescription = getRatingDescriptionFromValue(rating);
        this.starDisplay = getStarDisplayFromValue(rating);
        this.isPositive = rating != null && rating >= 4;
        this.isNegative = rating != null && rating <= 2;
        this.isNeutral = rating != null && rating == 3;
    }

    // Static factory method
    public static TicketRatingDto fromEntity(TicketRating rating) {
        return new TicketRatingDto(
            rating.getId(),
            rating.getRating(),
            rating.getFeedback(),
            rating.getTicket().getId(),
            UserDto.fromEntity(rating.getRatedBy()),
            rating.getCreatedAt()
        );
    }

    // Helper methods
    private String getRatingDescriptionFromValue(Integer rating) {
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

    private String getStarDisplayFromValue(Integer rating) {
        if (rating == null) return "☆☆☆☆☆";
        
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stars.append(i <= rating ? "★" : "☆");
        }
        return stars.toString();
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
        this.ratingDescription = getRatingDescriptionFromValue(rating);
        this.starDisplay = getStarDisplayFromValue(rating);
        this.isPositive = rating != null && rating >= 4;
        this.isNegative = rating != null && rating <= 2;
        this.isNeutral = rating != null && rating == 3;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UserDto getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(UserDto ratedBy) {
        this.ratedBy = ratedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRatingDescription() {
        return ratingDescription;
    }

    public void setRatingDescription(String ratingDescription) {
        this.ratingDescription = ratingDescription;
    }

    public String getStarDisplay() {
        return starDisplay;
    }

    public void setStarDisplay(String starDisplay) {
        this.starDisplay = starDisplay;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public void setNegative(boolean negative) {
        isNegative = negative;
    }

    public boolean isNeutral() {
        return isNeutral;
    }

    public void setNeutral(boolean neutral) {
        isNeutral = neutral;
    }
}
