package com.ticketsystem.dto;

import com.ticketsystem.entity.User;
import com.ticketsystem.entity.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdminUserDto {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private UserRole role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdTicketsCount;
    private Long assignedTicketsCount;
    private Long commentsCount;
    private LocalDateTime lastLoginAt;

    // Constructors
    public AdminUserDto() {}

    public AdminUserDto(UUID id, String email, String firstName, String lastName, UserRole role, 
                       Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Static factory method
    public static AdminUserDto fromEntity(User user) {
        return new AdminUserDto(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            user.getIsActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    public static AdminUserDto fromEntityWithStats(User user, Long createdTicketsCount, 
                                                  Long assignedTicketsCount, Long commentsCount) {
        AdminUserDto dto = fromEntity(user);
        dto.setCreatedTicketsCount(createdTicketsCount);
        dto.setAssignedTicketsCount(assignedTicketsCount);
        dto.setCommentsCount(commentsCount);
        return dto;
    }

    // Business methods
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }

    public boolean isSupportAgent() {
        return role != null && role.isSupportAgent();
    }

    public boolean isRegularUser() {
        return role != null && role.isUser();
    }

    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : "Unknown";
    }

    public String getStatusText() {
        return isActive ? "Active" : "Inactive";
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.fullName = firstName + " " + (lastName != null ? lastName : "");
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.fullName = (firstName != null ? firstName : "") + " " + lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Long getCreatedTicketsCount() {
        return createdTicketsCount;
    }

    public void setCreatedTicketsCount(Long createdTicketsCount) {
        this.createdTicketsCount = createdTicketsCount;
    }

    public Long getAssignedTicketsCount() {
        return assignedTicketsCount;
    }

    public void setAssignedTicketsCount(Long assignedTicketsCount) {
        this.assignedTicketsCount = assignedTicketsCount;
    }

    public Long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
