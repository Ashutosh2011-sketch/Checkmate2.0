package com.checkmate.backend.dto;

public class ChecklistSummaryDto {
    private Long id;
    private String title;
    private String assignee;
    private String status;
    private String deadline;
    private String priority;
    private String visibility;

    public ChecklistSummaryDto() {}

    public ChecklistSummaryDto(Long id, String title, String assignee, String status, Integer dueDays, String priority, String visibility) {
        this.id = id;
        this.title = title;
        this.assignee = assignee;
        this.status = status;
        this.deadline = formatDeadline(dueDays);
        this.priority = priority;
        this.visibility = visibility;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    private String formatDeadline(Integer dueDays) {
        if (dueDays == null) return "No Deadline";
        // Format as "DD MMM, YYYY" - assume dueDays from checklist creation date (now for simplicity)
        java.time.LocalDate dueDate = java.time.LocalDate.now().plusDays(dueDays);
        return dueDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM, yyyy"));
    }
}

