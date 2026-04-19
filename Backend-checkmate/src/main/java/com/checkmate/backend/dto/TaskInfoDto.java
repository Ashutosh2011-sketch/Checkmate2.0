package com.checkmate.backend.dto;

public class TaskInfoDto {

    private Long id;
    private String title;
    private String status;
    private String priority;
    private String checklistName;

    public TaskInfoDto() {}

    public TaskInfoDto(Long id, String title, String status, String priority, String checklistName) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.checklistName = checklistName;
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
}
