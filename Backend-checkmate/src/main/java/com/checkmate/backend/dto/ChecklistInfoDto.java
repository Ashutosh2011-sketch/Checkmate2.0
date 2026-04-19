package com.checkmate.backend.dto;

public class ChecklistInfoDto {

    private String name;
    private int progress;
    private int totalTasks;
    private int completedTasks;

    public ChecklistInfoDto() {}

    public ChecklistInfoDto(String name, int totalTasks, int completedTasks) {
        this.name = name;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.progress = totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0;
    }

    // ===== GETTERS & SETTERS =====

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
}
