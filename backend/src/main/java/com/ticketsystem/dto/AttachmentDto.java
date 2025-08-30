package com.ticketsystem.dto;

import com.ticketsystem.entity.Attachment;

import java.time.LocalDateTime;
import java.util.UUID;

public class AttachmentDto {

    private UUID id;
    private String fileName;
    private String originalFileName;
    private Long fileSize;
    private String formattedFileSize;
    private String mimeType;
    private UUID ticketId;
    private UserDto uploadedBy;
    private LocalDateTime createdAt;
    private boolean isImage;
    private boolean isDocument;
    private String fileExtension;

    // Constructors
    public AttachmentDto() {}

    public AttachmentDto(UUID id, String fileName, String originalFileName, Long fileSize,
                        String mimeType, UUID ticketId, UserDto uploadedBy, LocalDateTime createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.ticketId = ticketId;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
        
        // Set computed fields
        this.formattedFileSize = formatFileSize(fileSize);
        this.isImage = mimeType != null && mimeType.startsWith("image/");
        this.isDocument = isDocumentType(mimeType);
        this.fileExtension = getFileExtensionFromName(originalFileName);
    }

    // Static factory method
    public static AttachmentDto fromEntity(Attachment attachment) {
        return new AttachmentDto(
            attachment.getId(),
            attachment.getFileName(),
            attachment.getOriginalFileName(),
            attachment.getFileSize(),
            attachment.getMimeType(),
            attachment.getTicket().getId(),
            UserDto.fromEntity(attachment.getUploadedBy()),
            attachment.getCreatedAt()
        );
    }

    // Helper methods
    private String formatFileSize(Long bytes) {
        if (bytes == null || bytes == 0) return "0 B";
        
        if (bytes < 1024) return bytes + " B";
        
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private boolean isDocumentType(String mimeType) {
        return mimeType != null && (
            mimeType.startsWith("application/pdf") ||
            mimeType.startsWith("application/msword") ||
            mimeType.startsWith("application/vnd.openxmlformats-officedocument") ||
            mimeType.startsWith("text/")
        );
    }

    private String getFileExtensionFromName(String fileName) {
        if (fileName == null) return "";
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
        this.fileExtension = getFileExtensionFromName(originalFileName);
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        this.formattedFileSize = formatFileSize(fileSize);
    }

    public String getFormattedFileSize() {
        return formattedFileSize;
    }

    public void setFormattedFileSize(String formattedFileSize) {
        this.formattedFileSize = formattedFileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
        this.isImage = mimeType != null && mimeType.startsWith("image/");
        this.isDocument = isDocumentType(mimeType);
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UserDto getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(UserDto uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public boolean isDocument() {
        return isDocument;
    }

    public void setDocument(boolean document) {
        isDocument = document;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
