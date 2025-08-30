package com.ticketsystem.repository;

import com.ticketsystem.entity.Attachment;
import com.ticketsystem.entity.Ticket;
import com.ticketsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    // Find attachments by ticket
    List<Attachment> findByTicketOrderByCreatedAtDesc(Ticket ticket);
    
    Page<Attachment> findByTicket(Ticket ticket, Pageable pageable);

    // Find attachments by uploader
    Page<Attachment> findByUploadedBy(User uploadedBy, Pageable pageable);
    
    List<Attachment> findByUploadedByOrderByCreatedAtDesc(User uploadedBy);

    // Find attachments by file name
    List<Attachment> findByOriginalFileNameContainingIgnoreCase(String fileName);

    // Find attachments by MIME type
    List<Attachment> findByMimeTypeStartingWith(String mimeTypePrefix);

    // Find image attachments
    @Query("SELECT a FROM Attachment a WHERE a.mimeType LIKE 'image/%'")
    List<Attachment> findImageAttachments();

    // Find document attachments
    @Query("SELECT a FROM Attachment a WHERE " +
           "a.mimeType LIKE 'application/pdf%' OR " +
           "a.mimeType LIKE 'application/msword%' OR " +
           "a.mimeType LIKE 'application/vnd.openxmlformats-officedocument%' OR " +
           "a.mimeType LIKE 'text/%'")
    List<Attachment> findDocumentAttachments();

    // Find attachments by size range
    @Query("SELECT a FROM Attachment a WHERE a.fileSize BETWEEN :minSize AND :maxSize")
    List<Attachment> findAttachmentsBySizeRange(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);

    // Find large attachments
    @Query("SELECT a FROM Attachment a WHERE a.fileSize > :sizeThreshold ORDER BY a.fileSize DESC")
    List<Attachment> findLargeAttachments(@Param("sizeThreshold") Long sizeThreshold);

    // Find attachments in date range
    @Query("SELECT a FROM Attachment a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Page<Attachment> findAttachmentsUploadedBetween(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate,
                                                   Pageable pageable);

    // Search attachments
    @Query("SELECT a FROM Attachment a WHERE " +
           "LOWER(a.originalFileName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.mimeType) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Attachment> searchAttachments(@Param("search") String search, Pageable pageable);

    // Find attachments with filters
    @Query("SELECT a FROM Attachment a WHERE " +
           "(:ticket IS NULL OR a.ticket = :ticket) AND " +
           "(:uploadedBy IS NULL OR a.uploadedBy = :uploadedBy) AND " +
           "(:mimeType IS NULL OR a.mimeType LIKE CONCAT(:mimeType, '%')) AND " +
           "(:search IS NULL OR LOWER(a.originalFileName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Attachment> findAttachmentsWithFilters(@Param("ticket") Ticket ticket,
                                              @Param("uploadedBy") User uploadedBy,
                                              @Param("mimeType") String mimeType,
                                              @Param("search") String search,
                                              Pageable pageable);

    // Count attachments by ticket
    long countByTicket(Ticket ticket);

    // Count attachments by uploader
    long countByUploadedBy(User uploadedBy);

    // Calculate total file size by ticket
    @Query("SELECT SUM(a.fileSize) FROM Attachment a WHERE a.ticket = :ticket")
    Long getTotalFileSizeByTicket(@Param("ticket") Ticket ticket);

    // Calculate total file size by user
    @Query("SELECT SUM(a.fileSize) FROM Attachment a WHERE a.uploadedBy = :user")
    Long getTotalFileSizeByUser(@Param("user") User user);

    // Calculate total storage used
    @Query("SELECT SUM(a.fileSize) FROM Attachment a")
    Long getTotalStorageUsed();

    // Find recent attachments
    @Query("SELECT a FROM Attachment a WHERE a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<Attachment> findRecentAttachments(@Param("since") LocalDateTime since);

    // Find orphaned attachments (tickets that might have been deleted)
    @Query("SELECT a FROM Attachment a WHERE a.ticket IS NULL")
    List<Attachment> findOrphanedAttachments();

    // Statistics
    @Query("SELECT COUNT(a) FROM Attachment a WHERE a.createdAt >= :since")
    long countAttachmentsUploadedSince(@Param("since") LocalDateTime since);

    @Query("SELECT a.mimeType, COUNT(a) FROM Attachment a GROUP BY a.mimeType ORDER BY COUNT(a) DESC")
    List<Object[]> getAttachmentCountByMimeType();

    @Query("SELECT a.uploadedBy, COUNT(a) FROM Attachment a GROUP BY a.uploadedBy ORDER BY COUNT(a) DESC")
    List<Object[]> getMostActiveUploaders();

    // Find attachments by file extension
    @Query("SELECT a FROM Attachment a WHERE LOWER(a.originalFileName) LIKE LOWER(CONCAT('%.', :extension))")
    List<Attachment> findByFileExtension(@Param("extension") String extension);
}
