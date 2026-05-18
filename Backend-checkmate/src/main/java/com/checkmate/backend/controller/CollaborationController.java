package com.checkmate.backend.controller;

import com.checkmate.backend.entity.Task;
import com.checkmate.backend.entity.TaskAttachment;
import com.checkmate.backend.entity.TaskComment;
import com.checkmate.backend.repository.TaskAttachmentRepository;
import com.checkmate.backend.repository.TaskCommentRepository;
import com.checkmate.backend.repository.TaskRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/collaboration")
@CrossOrigin(origins = "${app.cors.allowed-origin:http://localhost:4200}")
public class CollaborationController {

    private final TaskRepository taskRepository;
    private final TaskCommentRepository commentRepository;
    private final TaskAttachmentRepository attachmentRepository;

    public CollaborationController(TaskRepository taskRepository,
            TaskCommentRepository commentRepository,
            TaskAttachmentRepository attachmentRepository) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.attachmentRepository = attachmentRepository;
    }

    // ==================== COMMENTS ====================

    @GetMapping("/tasks/{taskId}/comments")
    @Transactional
    public ResponseEntity<?> getComments(@PathVariable Long taskId) {
        try {
            List<TaskComment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (TaskComment c : comments) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", c.getId());
                map.put("taskId", taskId);
                map.put("authorName", c.getAuthorName());
                map.put("content", c.getContent());
                map.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : "");
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @PostMapping("/tasks/{taskId}/comments")
    @Transactional
    public ResponseEntity<?> addComment(@PathVariable Long taskId,
            @RequestBody Map<String, String> body) {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

            String content = body.get("content");
            String author = body.get("authorName");

            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Comment content is required"));
            }

            TaskComment comment = new TaskComment();
            comment.setTask(task);
            comment.setAuthorName(author != null ? author : "Anonymous");
            comment.setContent(content.trim());
            comment.setCreatedAt(LocalDateTime.now());
            comment = commentRepository.save(comment);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("id", comment.getId());
            result.put("taskId", taskId);
            result.put("authorName", comment.getAuthorName());
            result.put("content", comment.getContent());
            result.put("createdAt", comment.getCreatedAt().toString());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    @Transactional
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentRepository.deleteById(commentId);
            return ResponseEntity.ok(Map.of("message", "Comment deleted"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    // ==================== ATTACHMENTS ====================

    @GetMapping("/tasks/{taskId}/attachments")
    @Transactional
    public ResponseEntity<?> getAttachments(@PathVariable Long taskId) {
        try {
            List<TaskAttachment> attachments = attachmentRepository.findByTaskId(taskId);
            List<Map<String, Object>> result = new ArrayList<>();

            for (TaskAttachment a : attachments) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", a.getId());
                map.put("taskId", taskId);
                map.put("fileName", a.getFileName());
                map.put("fileType", a.getFileType());
                map.put("fileSize", a.getFileSize());
                map.put("uploadedBy", a.getUploadedBy());
                map.put("uploadedAt", a.getUploadedAt() != null ? a.getUploadedAt().toString() : "");
                map.put("sourceType", a.getSourceType());
                map.put("driveFileUrl", a.getDriveFileUrl());
                result.add(map);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    // ── Local file upload (existing) ──────────────────────────────────────────
    @PostMapping("/tasks/{taskId}/attachments")
    @Transactional
    public ResponseEntity<?> uploadAttachment(@PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadedBy", defaultValue = "User") String uploadedBy) {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "File size exceeds 10MB limit"));
            }

            TaskAttachment attachment = new TaskAttachment();
            attachment.setTask(task);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setFileData(file.getBytes());
            attachment.setSourceType("LOCAL");
            attachment.setUploadedBy(uploadedBy);
            attachment.setUploadedAt(LocalDateTime.now());
            attachment = attachmentRepository.save(attachment);

            return ResponseEntity.ok(buildAttachmentResponse(attachment, taskId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    // ── Google Drive link save (NEW) ──────────────────────────────────────────
    @PostMapping("/tasks/{taskId}/attachments/drive")
    @Transactional
    public ResponseEntity<?> saveDriveAttachment(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> body) {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

            String fileName = body.get("fileName");
            String driveUrl = body.get("driveFileUrl");
            String uploadedBy = body.getOrDefault("uploadedBy", "Admin");

            if (fileName == null || driveUrl == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "fileName and driveFileUrl are required"));
            }

            TaskAttachment attachment = new TaskAttachment();
            attachment.setTask(task);
            attachment.setFileName(fileName);
            attachment.setFileType("application/google-drive");
            attachment.setFileSize(0L);
            attachment.setDriveFileUrl(driveUrl);
            attachment.setSourceType("GOOGLE_DRIVE");
            attachment.setUploadedBy(uploadedBy);
            attachment.setUploadedAt(LocalDateTime.now());
            attachment = attachmentRepository.save(attachment);

            return ResponseEntity.ok(buildAttachmentResponse(attachment, taskId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @GetMapping("/attachments/{attachmentId}/download")
    @Transactional
    public ResponseEntity<?> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("Attachment not found: " + attachmentId));

            // Google Drive — redirect to URL
            if ("GOOGLE_DRIVE".equals(attachment.getSourceType())) {
                return ResponseEntity.status(302)
                        .header(HttpHeaders.LOCATION, attachment.getDriveFileUrl())
                        .build();
            }

            byte[] data = attachment.getFileData();
            if (data == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No file data stored"));
            }

            String contentType = attachment.getFileType() != null
                    ? attachment.getFileType()
                    : "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @DeleteMapping("/attachments/{attachmentId}")
    @Transactional
    public ResponseEntity<?> deleteAttachment(@PathVariable Long attachmentId) {
        try {
            attachmentRepository.deleteById(attachmentId);
            return ResponseEntity.ok(Map.of("message", "Attachment deleted"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @GetMapping("/tasks/{taskId}/counts")
    public ResponseEntity<?> getCounts(@PathVariable Long taskId) {
        try {
            long commentCount = commentRepository.countByTaskId(taskId);
            long attachmentCount = attachmentRepository.countByTaskId(taskId);
            return ResponseEntity.ok(Map.of(
                    "commentCount", commentCount,
                    "attachmentCount", attachmentCount));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("commentCount", 0, "attachmentCount", 0));
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private Map<String, Object> buildAttachmentResponse(TaskAttachment a, Long taskId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", a.getId());
        result.put("taskId", taskId);
        result.put("fileName", a.getFileName());
        result.put("fileType", a.getFileType());
        result.put("fileSize", a.getFileSize());
        result.put("uploadedBy", a.getUploadedBy());
        result.put("uploadedAt", a.getUploadedAt().toString());
        result.put("sourceType", a.getSourceType());
        result.put("driveFileUrl", a.getDriveFileUrl());
        return result;
    }
}