package com.checkmate.backend.dto;

import java.time.LocalDateTime;

public class TaskInfoDto {

    private Long id;
    private String title;
    private String status;
    private String priority;
    private String checklistName;
    private int completionPercent;
    private boolean completed;
    private LocalDateTime completedAt;
    private String completedBy;

    public TaskInfoDto() {}

    public TaskInfoDto(Long id, String title, String status, String priority, String checklistName,
                       int completionPercent, boolean completed) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.checklistName = checklistName;
        this.completionPercent = completionPercent;
        this.completed = completed;
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getChecklistName() { return checklistName; }
    public void setChecklistName(String checklistName) { this.checklistName = checklistName; }

    public int getCompletionPercent() { return completionPercent; }
    public void setCompletionPercent(int completionPercent) { this.completionPercent = completionPercent; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }
}
