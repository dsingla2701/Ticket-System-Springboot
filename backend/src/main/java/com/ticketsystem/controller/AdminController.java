package com.ticketsystem.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ticketsystem.dto.AdminUserDto;
import com.ticketsystem.dto.CreateUserRequest;
import com.ticketsystem.dto.SystemStatsDto;
import com.ticketsystem.dto.UpdateUserRequest;
import com.ticketsystem.entity.User;
import com.ticketsystem.entity.UserRole;
import com.ticketsystem.service.AdminService;
import com.ticketsystem.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrative endpoints for system management")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    // System Statistics
    @GetMapping("/stats")
    @Operation(summary = "Get system statistics", description = "Retrieve comprehensive system statistics and metrics")
    public ResponseEntity<SystemStatsDto> getSystemStats() {
        SystemStatsDto stats = adminService.getSystemStats();
        return ResponseEntity.ok(stats);
    }

    // User Management
    @PostMapping("/users")
    @Operation(summary = "Create user", description = "Create a new user account")
    public ResponseEntity<AdminUserDto> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal User currentUser) {

        User user = adminService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdminUserDto.fromEntity(user));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve all users with filtering and pagination")
    public ResponseEntity<Page<AdminUserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users;
        if (role != null || isActive != null || search != null) {
            users = adminService.getUsersWithFilters(role, isActive, search, pageable);
        } else {
            users = adminService.getAllUsers(pageable);
        }

        Page<AdminUserDto> userDtos = users.map(AdminUserDto::fromEntity);
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID")
    public ResponseEntity<AdminUserDto> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(AdminUserDto.fromEntity(user));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<AdminUserDto> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal User currentUser) {

        User user = adminService.updateUser(id, request);
        return ResponseEntity.ok(AdminUserDto.fromEntity(user));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user", description = "Delete a user account")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/search")
    @Operation(summary = "Search users", description = "Search users by name or email")
    public ResponseEntity<Page<AdminUserDto>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users = adminService.searchUsers(query, pageable);
        Page<AdminUserDto> userDtos = users.map(AdminUserDto::fromEntity);
        return ResponseEntity.ok(userDtos);
    }

    // Bulk Operations
    @PutMapping("/users/bulk/status")
    @Operation(summary = "Bulk update user status", description = "Update status for multiple users")
    public ResponseEntity<Void> bulkUpdateUserStatus(
            @RequestParam List<UUID> userIds,
            @RequestParam boolean isActive,
            @AuthenticationPrincipal User currentUser) {

        adminService.bulkUpdateUserStatus(userIds, isActive);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/bulk/role")
    @Operation(summary = "Bulk update user role", description = "Update role for multiple users")
    public ResponseEntity<Void> bulkUpdateUserRole(
            @RequestParam List<UUID> userIds,
            @RequestParam UserRole role,
            @AuthenticationPrincipal User currentUser) {

        adminService.bulkUpdateUserRole(userIds, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/bulk")
    @Operation(summary = "Bulk delete users", description = "Delete multiple users")
    public ResponseEntity<Void> bulkDeleteUsers(
            @RequestParam List<UUID> userIds,
            @AuthenticationPrincipal User currentUser) {

        adminService.bulkDeleteUsers(userIds);
        return ResponseEntity.noContent().build();
    }

    // System Health and Monitoring
    @GetMapping("/health")
    @Operation(summary = "System health check", description = "Check system health and status")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("version", "1.0.0");

        try {
            SystemStatsDto stats = adminService.getSystemStats();
            health.put("totalUsers", stats.getTotalUsers());
            health.put("totalTickets", stats.getTotalTickets());
            health.put("activeTickets", stats.getOpenTickets() + stats.getInProgressTickets());
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
        }

        return ResponseEntity.ok(health);
    }

    @GetMapping("/activity")
    @Operation(summary = "Recent system activity", description = "Get recent system activity and events")
    public ResponseEntity<Map<String, Object>> getRecentActivity() {
        Map<String, Object> activity = new HashMap<>();

        try {
            SystemStatsDto stats = adminService.getSystemStats();
            activity.put("newTicketsToday", stats.getNewTicketsToday());
            activity.put("newUsersToday", stats.getNewUsersToday());
            activity.put("lastTicketCreated", stats.getLastTicketCreated());
            activity.put("lastUserRegistered", stats.getLastUserRegistered());
            activity.put("overdueTickets", stats.getOverdueTickets());
            activity.put("unassignedTickets", stats.getUnassignedTickets());
        } catch (Exception e) {
            activity.put("error", e.getMessage());
        }

        return ResponseEntity.ok(activity);
    }
}
