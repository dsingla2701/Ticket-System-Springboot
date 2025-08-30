package com.ticketsystem.entity;

public enum TicketPriority {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4);

    private final String displayName;
    private final int level;

    TicketPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public boolean isLow() {
        return this == LOW;
    }

    public boolean isMedium() {
        return this == MEDIUM;
    }

    public boolean isHigh() {
        return this == HIGH;
    }

    public boolean isUrgent() {
        return this == URGENT;
    }

    public boolean isHigherThan(TicketPriority other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(TicketPriority other) {
        return this.level < other.level;
    }

    public String getCssClass() {
        switch (this) {
            case LOW:
                return "priority-low";
            case MEDIUM:
                return "priority-medium";
            case HIGH:
                return "priority-high";
            case URGENT:
                return "priority-urgent";
            default:
                return "priority-medium";
        }
    }

    public String getColorCode() {
        switch (this) {
            case LOW:
                return "#10B981"; // Green
            case MEDIUM:
                return "#F59E0B"; // Yellow
            case HIGH:
                return "#EF4444"; // Red
            case URGENT:
                return "#DC2626"; // Dark Red
            default:
                return "#F59E0B";
        }
    }
}
