package com.ticketsystem.controller;

import com.ticketsystem.dto.UpdateUserRequest;
import com.ticketsystem.dto.UserDto;
import com.ticketsystem.entity.User;
import com.ticketsystem.entity.UserRole;
import com.ticketsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Get the profile of the currently authenticated user")
    public ResponseEntity<UserDto> getCurrentUserProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(UserDto.fromEntity(currentUser));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile", description = "Update the profile of the currently authenticated user")
    public ResponseEntity<UserDto> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        User updatedUser = userService.updateUser(
            currentUser.getId(),
            request.getFirstName() != null ? request.getFirstName() : currentUser.getFirstName(),
            request.getLastName() != null ? request.getLastName() : currentUser.getLastName(),
            request.getEmail() != null ? request.getEmail() : currentUser.getEmail()
        );
        
        return ResponseEntity.ok(UserDto.fromEntity(updatedUser));
    }

    @PutMapping("/profile/password")
    @Operation(summary = "Change password", description = "Change the password of the currently authenticated user")
    public ResponseEntity<Void> changePassword(
            @RequestParam String newPassword,
            @AuthenticationPrincipal User currentUser) {
        
        userService.updateUserPassword(currentUser.getId(), newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/support-agents")
    @Operation(summary = "Get support agents", description = "Get list of active support agents")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getSupportAgents() {
        List<User> agents = userService.getSupportAgents();
        List<UserDto> agentDtos = agents.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(agentDtos);
    }

    @GetMapping("/admins")
    @Operation(summary = "Get administrators", description = "Get list of active administrators")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAdmins() {
        List<User> admins = userService.getAdmins();
        List<UserDto> adminDtos = admins.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(adminDtos);
    }

    @GetMapping("/by-role/{role}")
    @Operation(summary = "Get users by role", description = "Get users by specific role")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable UserRole role) {
        List<User> users = userService.getUsersByRole(role);
        List<UserDto> userDtos = users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get user information by ID (limited access)")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name or email")
    @PreAuthorize("hasRole('SUPPORT_AGENT') or hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<User> users = userService.searchUsers(query, pageable);
        Page<UserDto> userDtos = users.map(UserDto::fromEntity);
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check email availability", description = "Check if an email address is available for registration")
    public ResponseEntity<Boolean> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Get statistics for the current user")
    public ResponseEntity<UserStatsDto> getUserStats(@AuthenticationPrincipal User currentUser) {
        UserStatsDto stats = new UserStatsDto();
        stats.setCreatedTicketsCount(userService.getCreatedTicketCount(currentUser));
        
        if (currentUser.getRole().hasSupportPrivileges()) {
            stats.setAssignedTicketsCount(userService.getAssignedTicketCount(currentUser));
        }
        
        return ResponseEntity.ok(stats);
    }

    // Inner class for user statistics
    public static class UserStatsDto {
        private Long createdTicketsCount;
        private Long assignedTicketsCount;
        private Long commentsCount;

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
    }
}
