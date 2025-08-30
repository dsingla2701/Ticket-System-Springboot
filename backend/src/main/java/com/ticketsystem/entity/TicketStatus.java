package com.ticketsystem.entity;

public enum TicketStatus {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    CLOSED("Closed");

    private final String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isOpen() {
        return this == OPEN;
    }

    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }

    public boolean isResolved() {
        return this == RESOLVED;
    }

    public boolean isClosed() {
        return this == CLOSED;
    }

    public boolean canTransitionTo(TicketStatus newStatus) {
        switch (this) {
            case OPEN:
                return newStatus == IN_PROGRESS || newStatus == CLOSED;
            case IN_PROGRESS:
                return newStatus == RESOLVED || newStatus == OPEN || newStatus == CLOSED;
            case RESOLVED:
                return newStatus == CLOSED || newStatus == IN_PROGRESS;
            case CLOSED:
                return false; // Closed tickets cannot be reopened
            default:
                return false;
        }
    }

    public boolean isActive() {
        return this == OPEN || this == IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this == RESOLVED || this == CLOSED;
    }
}
