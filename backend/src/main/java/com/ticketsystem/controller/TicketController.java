package com.ticketsystem.controller;

import com.ticketsystem.dto.*;
import com.ticketsystem.entity.*;
import com.ticketsystem.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@Tag(name = "Tickets", description = "Ticket management endpoints")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    @Operation(summary = "Create a new ticket", description = "Create a new support ticket")
    public ResponseEntity<TicketDto> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        Ticket ticket = ticketService.createTicket(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketDto.fromEntity(ticket));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID", description = "Retrieve a specific ticket by its ID")
    public ResponseEntity<TicketDto> getTicket(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        
        Ticket ticket = ticketService.getTicketByIdWithAccess(id, currentUser);
        return ResponseEntity.ok(TicketDto.fromEntityWithDetails(ticket));
    }

    @GetMapping
    @Operation(summary = "Get tickets", description = "Get tickets based on user role and permissions")
    public ResponseEntity<Page<TicketDto>> getTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) UUID assignedToId,
            @RequestParam(required = false) UUID createdById,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal User currentUser) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Ticket> tickets;
        
        if (status != null || priority != null || assignedToId != null || createdById != null || search != null) {
            User assignedTo = assignedToId != null ? new User() {{ setId(assignedToId); }} : null;
            User createdBy = createdById != null ? new User() {{ setId(createdById); }} : null;
            tickets = ticketService.getTicketsWithFilters(status, priority, assignedTo, createdBy, search, pageable);
        } else {
            tickets = ticketService.getTicketsForUser(currentUser, pageable);
        }
        
        Page<TicketDto> ticketDtos = tickets.map(TicketDto::fromEntity);
        return ResponseEntity.ok(ticketDtos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ticket", description = "Update ticket details")
    public ResponseEntity<TicketDto> updateTicket(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTicketRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        Ticket ticket = ticketService.updateTicket(id, request, currentUser);
        return ResponseEntity.ok(TicketDto.fromEntity(ticket));
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign ticket", description = "Assign ticket to a support agent")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<TicketDto> assignTicket(
            @PathVariable UUID id,
            @RequestParam UUID agentId,
            @AuthenticationPrincipal User currentUser) {
        
        Ticket ticket = ticketService.assignTicket(id, agentId, currentUser);
        return ResponseEntity.ok(TicketDto.fromEntity(ticket));
    }

    @PutMapping("/{id}/unassign")
    @Operation(summary = "Unassign ticket", description = "Remove assignment from ticket")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<TicketDto> unassignTicket(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        
        Ticket ticket = ticketService.unassignTicket(id, currentUser);
        return ResponseEntity.ok(TicketDto.fromEntity(ticket));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update ticket status", description = "Change ticket status")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<TicketDto> updateTicketStatus(
            @PathVariable UUID id,
            @RequestParam TicketStatus status,
            @AuthenticationPrincipal User currentUser) {
        
        Ticket ticket = ticketService.updateTicketStatus(id, status, currentUser);
        return ResponseEntity.ok(TicketDto.fromEntity(ticket));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ticket", description = "Delete a ticket (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        
        ticketService.deleteTicket(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assigned")
    @Operation(summary = "Get assigned tickets", description = "Get tickets assigned to current user")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<Page<TicketDto>> getAssignedTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "priority") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal User currentUser) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Ticket> tickets = ticketService.getAssignedTickets(currentUser, pageable);
        Page<TicketDto> ticketDtos = tickets.map(TicketDto::fromEntity);
        return ResponseEntity.ok(ticketDtos);
    }

    @GetMapping("/unassigned")
    @Operation(summary = "Get unassigned tickets", description = "Get tickets that are not assigned to anyone")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<Page<TicketDto>> getUnassignedTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "priority") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Ticket> tickets = ticketService.getUnassignedTickets(pageable);
        Page<TicketDto> ticketDtos = tickets.map(TicketDto::fromEntity);
        return ResponseEntity.ok(ticketDtos);
    }

    @GetMapping("/search")
    @Operation(summary = "Search tickets", description = "Search tickets by subject and description")
    public ResponseEntity<Page<TicketDto>> searchTickets(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal User currentUser) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Ticket> tickets = ticketService.searchTickets(query, pageable);
        Page<TicketDto> ticketDtos = tickets.map(TicketDto::fromEntity);
        return ResponseEntity.ok(ticketDtos);
    }
}
