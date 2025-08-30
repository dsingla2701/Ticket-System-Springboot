package com.ticketsystem.entity;

public enum UserRole {
    USER("User"),
    SUPPORT_AGENT("Support Agent"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isSupportAgent() {
        return this == SUPPORT_AGENT;
    }

    public boolean isUser() {
        return this == USER;
    }

    public boolean hasAdminPrivileges() {
        return this == ADMIN;
    }

    public boolean hasSupportPrivileges() {
        return this == ADMIN || this == SUPPORT_AGENT;
    }
}
