package com.ticketsystem.dto;

import com.ticketsystem.entity.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDto {

    private UUID id;
    private String content;
    private UUID ticketId;
    private UserDto author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isEdited;
    private boolean isSystemComment;

    // Constructors
    public CommentDto() {}

    public CommentDto(UUID id, String content, UUID ticketId, UserDto author,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.ticketId = ticketId;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isEdited = updatedAt != null && !updatedAt.equals(createdAt);
        this.isSystemComment = content != null && content.startsWith("[SYSTEM]");
    }

    // Static factory method
    public static CommentDto fromEntity(Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getContent(),
            comment.getTicket().getId(),
            UserDto.fromEntity(comment.getAuthor()),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.isSystemComment = content != null && content.startsWith("[SYSTEM]");
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UserDto getAuthor() {
        return author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
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
        this.isEdited = updatedAt != null && createdAt != null && !updatedAt.equals(createdAt);
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public boolean isSystemComment() {
        return isSystemComment;
    }

    public void setSystemComment(boolean systemComment) {
        isSystemComment = systemComment;
    }
}
