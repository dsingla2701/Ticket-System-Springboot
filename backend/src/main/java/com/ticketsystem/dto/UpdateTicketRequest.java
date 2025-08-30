package com.ticketsystem.dto;

import com.ticketsystem.entity.TicketPriority;
import com.ticketsystem.entity.TicketStatus;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class UpdateTicketRequest {

    @Size(max = 255, message = "Subject must not exceed 255 characters")
    private String subject;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    private TicketStatus status;

    private TicketPriority priority;

    private UUID assignedToId;

    // Constructors
    public UpdateTicketRequest() {}

    public UpdateTicketRequest(String subject, String description, TicketStatus status, 
                              TicketPriority priority, UUID assignedToId) {
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assignedToId = assignedToId;
    }

    // Getters and Setters
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

    public UUID getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(UUID assignedToId) {
        this.assignedToId = assignedToId;
    }
}
