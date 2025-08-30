package com.ticketsystem.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class SystemStatsDto {

    // User Statistics
    private Long totalUsers;
    private Long activeUsers;
    private Long adminUsers;
    private Long supportAgents;
    private Long regularUsers;
    private Long newUsersToday;
    private Long newUsersThisWeek;
    private Long newUsersThisMonth;

    // Ticket Statistics
    private Long totalTickets;
    private Long openTickets;
    private Long inProgressTickets;
    private Long resolvedTickets;
    private Long closedTickets;
    private Long unassignedTickets;
    private Long overdueTickets;
    private Long newTicketsToday;
    private Long newTicketsThisWeek;
    private Long newTicketsThisMonth;

    // Priority Statistics
    private Long lowPriorityTickets;
    private Long mediumPriorityTickets;
    private Long highPriorityTickets;
    private Long urgentPriorityTickets;

    // Performance Statistics
    private Double averageResolutionTimeHours;
    private Double averageResponseTimeHours;
    private Double customerSatisfactionRate;
    private Long totalComments;
    private Long totalAttachments;
    private Long totalStorageUsedMB;

    // Recent Activity
    private LocalDateTime lastTicketCreated;
    private LocalDateTime lastTicketResolved;
    private LocalDateTime lastUserRegistered;

    // Distribution Maps
    private Map<String, Long> ticketsByStatus;
    private Map<String, Long> ticketsByPriority;
    private Map<String, Long> usersByRole;
    private Map<String, Long> ticketsCreatedByDay; // Last 7 days
    private Map<String, Long> ticketsResolvedByDay; // Last 7 days

    // Constructors
    public SystemStatsDto() {}

    // Getters and Setters
    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(Long adminUsers) {
        this.adminUsers = adminUsers;
    }

    public Long getSupportAgents() {
        return supportAgents;
    }

    public void setSupportAgents(Long supportAgents) {
        this.supportAgents = supportAgents;
    }

    public Long getRegularUsers() {
        return regularUsers;
    }

    public void setRegularUsers(Long regularUsers) {
        this.regularUsers = regularUsers;
    }

    public Long getNewUsersToday() {
        return newUsersToday;
    }

    public void setNewUsersToday(Long newUsersToday) {
        this.newUsersToday = newUsersToday;
    }

    public Long getNewUsersThisWeek() {
        return newUsersThisWeek;
    }

    public void setNewUsersThisWeek(Long newUsersThisWeek) {
        this.newUsersThisWeek = newUsersThisWeek;
    }

    public Long getNewUsersThisMonth() {
        return newUsersThisMonth;
    }

    public void setNewUsersThisMonth(Long newUsersThisMonth) {
        this.newUsersThisMonth = newUsersThisMonth;
    }

    public Long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Long getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(Long openTickets) {
        this.openTickets = openTickets;
    }

    public Long getInProgressTickets() {
        return inProgressTickets;
    }

    public void setInProgressTickets(Long inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }

    public Long getResolvedTickets() {
        return resolvedTickets;
    }

    public void setResolvedTickets(Long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    public Long getClosedTickets() {
        return closedTickets;
    }

    public void setClosedTickets(Long closedTickets) {
        this.closedTickets = closedTickets;
    }

    public Long getUnassignedTickets() {
        return unassignedTickets;
    }

    public void setUnassignedTickets(Long unassignedTickets) {
        this.unassignedTickets = unassignedTickets;
    }

    public Long getOverdueTickets() {
        return overdueTickets;
    }

    public void setOverdueTickets(Long overdueTickets) {
        this.overdueTickets = overdueTickets;
    }

    public Long getNewTicketsToday() {
        return newTicketsToday;
    }

    public void setNewTicketsToday(Long newTicketsToday) {
        this.newTicketsToday = newTicketsToday;
    }

    public Long getNewTicketsThisWeek() {
        return newTicketsThisWeek;
    }

    public void setNewTicketsThisWeek(Long newTicketsThisWeek) {
        this.newTicketsThisWeek = newTicketsThisWeek;
    }

    public Long getNewTicketsThisMonth() {
        return newTicketsThisMonth;
    }

    public void setNewTicketsThisMonth(Long newTicketsThisMonth) {
        this.newTicketsThisMonth = newTicketsThisMonth;
    }

    public Long getLowPriorityTickets() {
        return lowPriorityTickets;
    }

    public void setLowPriorityTickets(Long lowPriorityTickets) {
        this.lowPriorityTickets = lowPriorityTickets;
    }

    public Long getMediumPriorityTickets() {
        return mediumPriorityTickets;
    }

    public void setMediumPriorityTickets(Long mediumPriorityTickets) {
        this.mediumPriorityTickets = mediumPriorityTickets;
    }

    public Long getHighPriorityTickets() {
        return highPriorityTickets;
    }

    public void setHighPriorityTickets(Long highPriorityTickets) {
        this.highPriorityTickets = highPriorityTickets;
    }

    public Long getUrgentPriorityTickets() {
        return urgentPriorityTickets;
    }

    public void setUrgentPriorityTickets(Long urgentPriorityTickets) {
        this.urgentPriorityTickets = urgentPriorityTickets;
    }

    public Double getAverageResolutionTimeHours() {
        return averageResolutionTimeHours;
    }

    public void setAverageResolutionTimeHours(Double averageResolutionTimeHours) {
        this.averageResolutionTimeHours = averageResolutionTimeHours;
    }

    public Double getAverageResponseTimeHours() {
        return averageResponseTimeHours;
    }

    public void setAverageResponseTimeHours(Double averageResponseTimeHours) {
        this.averageResponseTimeHours = averageResponseTimeHours;
    }

    public Double getCustomerSatisfactionRate() {
        return customerSatisfactionRate;
    }

    public void setCustomerSatisfactionRate(Double customerSatisfactionRate) {
        this.customerSatisfactionRate = customerSatisfactionRate;
    }

    public Long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }

    public Long getTotalAttachments() {
        return totalAttachments;
    }

    public void setTotalAttachments(Long totalAttachments) {
        this.totalAttachments = totalAttachments;
    }

    public Long getTotalStorageUsedMB() {
        return totalStorageUsedMB;
    }

    public void setTotalStorageUsedMB(Long totalStorageUsedMB) {
        this.totalStorageUsedMB = totalStorageUsedMB;
    }

    public LocalDateTime getLastTicketCreated() {
        return lastTicketCreated;
    }

    public void setLastTicketCreated(LocalDateTime lastTicketCreated) {
        this.lastTicketCreated = lastTicketCreated;
    }

    public LocalDateTime getLastTicketResolved() {
        return lastTicketResolved;
    }

    public void setLastTicketResolved(LocalDateTime lastTicketResolved) {
        this.lastTicketResolved = lastTicketResolved;
    }

    public LocalDateTime getLastUserRegistered() {
        return lastUserRegistered;
    }

    public void setLastUserRegistered(LocalDateTime lastUserRegistered) {
        this.lastUserRegistered = lastUserRegistered;
    }

    public Map<String, Long> getTicketsByStatus() {
        return ticketsByStatus;
    }

    public void setTicketsByStatus(Map<String, Long> ticketsByStatus) {
        this.ticketsByStatus = ticketsByStatus;
    }

    public Map<String, Long> getTicketsByPriority() {
        return ticketsByPriority;
    }

    public void setTicketsByPriority(Map<String, Long> ticketsByPriority) {
        this.ticketsByPriority = ticketsByPriority;
    }

    public Map<String, Long> getUsersByRole() {
        return usersByRole;
    }

    public void setUsersByRole(Map<String, Long> usersByRole) {
        this.usersByRole = usersByRole;
    }

    public Map<String, Long> getTicketsCreatedByDay() {
        return ticketsCreatedByDay;
    }

    public void setTicketsCreatedByDay(Map<String, Long> ticketsCreatedByDay) {
        this.ticketsCreatedByDay = ticketsCreatedByDay;
    }

    public Map<String, Long> getTicketsResolvedByDay() {
        return ticketsResolvedByDay;
    }

    public void setTicketsResolvedByDay(Map<String, Long> ticketsResolvedByDay) {
        this.ticketsResolvedByDay = ticketsResolvedByDay;
    }
}
