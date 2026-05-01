package com.checkmate.backend.dto;

public class ChecklistInfoDto {

    private Long checklistId;
    private String name;
    private int progress;
    private int totalTasks;
    private int completedTasks;
    private String status; // "In Progress" or "Completed"

    public ChecklistInfoDto() {}

    public ChecklistInfoDto(Long checklistId, String name, int totalTasks, int completedTasks) {
        this.checklistId = checklistId;
        this.name = name;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.progress = totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0;
        this.status = this.progress >= 100 ? "Completed" : "In Progress";
    }

    // ===== GETTERS & SETTERS =====

    public Long getChecklistId() { return checklistId; }
    public void setChecklistId(Long checklistId) { this.checklistId = checklistId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
