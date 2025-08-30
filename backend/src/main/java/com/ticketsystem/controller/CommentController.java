package com.ticketsystem.controller;

import com.ticketsystem.dto.CommentDto;
import com.ticketsystem.dto.CreateCommentRequest;
import com.ticketsystem.entity.Comment;
import com.ticketsystem.entity.User;
import com.ticketsystem.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/tickets/{ticketId}")
    @Operation(summary = "Add comment to ticket", description = "Add a new comment to a specific ticket")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable UUID ticketId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        Comment comment = commentService.addComment(ticketId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentDto.fromEntity(comment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID", description = "Retrieve a specific comment by its ID")
    public ResponseEntity<CommentDto> getComment(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        
        Comment comment = commentService.getCommentByIdWithAccess(id, currentUser);
        return ResponseEntity.ok(CommentDto.fromEntity(comment));
    }

    @GetMapping("/tickets/{ticketId}")
    @Operation(summary = "Get comments for ticket", description = "Get all comments for a specific ticket")
    public ResponseEntity<List<CommentDto>> getCommentsByTicket(
            @PathVariable UUID ticketId,
            @AuthenticationPrincipal User currentUser) {
        
        List<Comment> comments = commentService.getCommentsByTicket(ticketId, currentUser);
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDtos);
    }

    @GetMapping("/tickets/{ticketId}/paginated")
    @Operation(summary = "Get paginated comments for ticket", description = "Get paginated comments for a specific ticket")
    public ResponseEntity<Page<CommentDto>> getCommentsByTicketPaginated(
            @PathVariable UUID ticketId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @AuthenticationPrincipal User currentUser) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Comment> comments = commentService.getCommentsByTicket(ticketId, currentUser, pageable);
        Page<CommentDto> commentDtos = comments.map(CommentDto::fromEntity);
        return ResponseEntity.ok(commentDtos);
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get comments by author", description = "Get all comments by a specific author")
    public ResponseEntity<Page<CommentDto>> getCommentsByAuthor(
            @PathVariable UUID authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal User currentUser) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        User author = new User();
        author.setId(authorId);
        
        Page<Comment> comments = commentService.getCommentsByAuthor(author, pageable);
        Page<CommentDto> commentDtos = comments.map(CommentDto::fromEntity);
        return ResponseEntity.ok(commentDtos);
    }

    @GetMapping("/search")
    @Operation(summary = "Search comments", description = "Search comments by content")
    public ResponseEntity<Page<CommentDto>> searchComments(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Comment> comments = commentService.searchComments(query, pageable);
        Page<CommentDto> commentDtos = comments.map(CommentDto::fromEntity);
        return ResponseEntity.ok(commentDtos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update comment", description = "Update comment content")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable UUID id,
            @RequestParam String content,
            @AuthenticationPrincipal User currentUser) {
        
        Comment comment = commentService.updateComment(id, content, currentUser);
        return ResponseEntity.ok(CommentDto.fromEntity(comment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Delete a comment")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        
        commentService.deleteComment(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tickets/{ticketId}/recent")
    @Operation(summary = "Get recent comments", description = "Get recent comments for a ticket")
    public ResponseEntity<List<CommentDto>> getRecentComments(
            @PathVariable UUID ticketId,
            @RequestParam(defaultValue = "24") int hours,
            @AuthenticationPrincipal User currentUser) {
        
        List<Comment> comments = commentService.getRecentCommentsByTicket(ticketId, currentUser, hours);
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDtos);
    }

    @GetMapping("/tickets/{ticketId}/count")
    @Operation(summary = "Get comment count", description = "Get total number of comments for a ticket")
    public ResponseEntity<Long> getCommentCount(
            @PathVariable UUID ticketId,
            @AuthenticationPrincipal User currentUser) {
        
        long count = commentService.getCommentCountByTicket(ticketId, currentUser);
        return ResponseEntity.ok(count);
    }
}
