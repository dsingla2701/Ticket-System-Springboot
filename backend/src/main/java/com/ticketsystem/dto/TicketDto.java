package com.ticketsystem.dto;

import com.ticketsystem.entity.Ticket;
import com.ticketsystem.entity.TicketPriority;
import com.ticketsystem.entity.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketDto {

    private UUID id;
    private String subject;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private UserDto createdBy;
    private UserDto assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private List<CommentDto> comments;
    private List<AttachmentDto> attachments;
    private TicketRatingDto rating;
    private long commentCount;
    private long attachmentCount;

    // Constructors
    public TicketDto() {}

    public TicketDto(UUID id, String subject, String description, TicketStatus status, 
                    TicketPriority priority, UserDto createdBy, UserDto assignedTo,
                    LocalDateTime createdAt, LocalDateTime updatedAt, 
                    LocalDateTime resolvedAt, LocalDateTime closedAt) {
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
        this.closedAt = closedAt;
    }

    // Static factory methods
    public static TicketDto fromEntity(Ticket ticket) {
        TicketDto dto = new TicketDto(
            ticket.getId(),
            ticket.getSubject(),
            ticket.getDescription(),
            ticket.getStatus(),
            ticket.getPriority(),
            UserDto.fromEntity(ticket.getCreatedBy()),
            ticket.getAssignedTo() != null ? UserDto.fromEntity(ticket.getAssignedTo()) : null,
            ticket.getCreatedAt(),
            ticket.getUpdatedAt(),
            ticket.getResolvedAt(),
            ticket.getClosedAt()
        );
        
        dto.setCommentCount(ticket.getCommentCount());
        dto.setAttachmentCount(ticket.getAttachmentCount());
        
        return dto;
    }

    public static TicketDto fromEntityWithDetails(Ticket ticket) {
        TicketDto dto = fromEntity(ticket);
        
        if (ticket.getComments() != null) {
            dto.setComments(ticket.getComments().stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList()));
        }
        
        if (ticket.getAttachments() != null) {
            dto.setAttachments(ticket.getAttachments().stream()
                .map(AttachmentDto::fromEntity)
                .collect(Collectors.toList()));
        }
        
        if (ticket.getRating() != null) {
            dto.setRating(TicketRatingDto.fromEntity(ticket.getRating()));
        }
        
        return dto;
    }

    // Business methods
    public boolean isOpen() {
        return status != null && status.isOpen();
    }

    public boolean isInProgress() {
        return status != null && status.isInProgress();
    }

    public boolean isResolved() {
        return status != null && status.isResolved();
    }

    public boolean isClosed() {
        return status != null && status.isClosed();
    }

    public boolean isActive() {
        return status != null && status.isActive();
    }

    public boolean isCompleted() {
        return status != null && status.isCompleted();
    }

    public boolean isAssigned() {
        return assignedTo != null;
    }

    public boolean isHighPriority() {
        return priority != null && (priority.isHigh() || priority.isUrgent());
    }

    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "Unknown";
    }

    public String getPriorityDisplayName() {
        return priority != null ? priority.getDisplayName() : "Unknown";
    }

    public String getPriorityColorCode() {
        return priority != null ? priority.getColorCode() : "#F59E0B";
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public UserDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDto createdBy) {
        this.createdBy = createdBy;
    }

    public UserDto getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserDto assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDto> attachments) {
        this.attachments = attachments;
    }

    public TicketRatingDto getRating() {
        return rating;
    }

    public void setRating(TicketRatingDto rating) {
        this.rating = rating;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(long attachmentCount) {
        this.attachmentCount = attachmentCount;
    }
}
