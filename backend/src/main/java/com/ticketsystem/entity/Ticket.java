package com.ticketsystem.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@EntityListeners(AuditingEntityListener.class)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority = TicketPriority.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TicketRating rating;

    // Constructors
    public Ticket() {}

    public Ticket(String subject, String description, TicketPriority priority, User createdBy) {
        this.subject = subject;
        this.description = description;
        this.priority = priority;
        this.createdBy = createdBy;
    }

    // Business methods
    public void updateStatus(TicketStatus newStatus) {
        if (this.status.canTransitionTo(newStatus)) {
            this.status = newStatus;
            
            if (newStatus == TicketStatus.RESOLVED) {
                this.resolvedAt = LocalDateTime.now();
            } else if (newStatus == TicketStatus.CLOSED) {
                this.closedAt = LocalDateTime.now();
                if (this.resolvedAt == null) {
                    this.resolvedAt = LocalDateTime.now();
                }
            }
        } else {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }
    }

    public void assignTo(User agent) {
        this.assignedTo = agent;
        if (this.status == TicketStatus.OPEN) {
            this.status = TicketStatus.IN_PROGRESS;
        }
    }

    public void unassign() {
        this.assignedTo = null;
        if (this.status == TicketStatus.IN_PROGRESS) {
            this.status = TicketStatus.OPEN;
        }
    }

    public boolean isAssigned() {
        return this.assignedTo != null;
    }

    public boolean canBeEditedBy(User user) {
        return user.getRole().hasAdminPrivileges() || 
               user.getRole().hasSupportPrivileges() || 
               user.equals(this.createdBy);
    }

    public boolean canBeAssignedBy(User user) {
        return user.getRole().hasSupportPrivileges();
    }

    public boolean canChangeStatus(User user) {
        return user.getRole().hasSupportPrivileges() || 
               (user.equals(this.assignedTo) && this.isAssigned());
    }

    public long getCommentCount() {
        return comments != null ? comments.size() : 0;
    }

    public long getAttachmentCount() {
        return attachments != null ? attachments.size() : 0;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public TicketRating getRating() {
        return rating;
    }

    public void setRating(TicketRating rating) {
        this.rating = rating;
    }
}
