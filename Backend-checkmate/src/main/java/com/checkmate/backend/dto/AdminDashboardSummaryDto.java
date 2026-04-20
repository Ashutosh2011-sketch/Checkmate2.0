package com.checkmate.backend.dto;

public class AdminDashboardSummaryDto {

    private long totalChecklists;
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long completedChecklists;

    public long getTotalChecklists() {
        return totalChecklists;
    }

    public void setTotalChecklists(long totalChecklists) {
        this.totalChecklists = totalChecklists;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public long getCompletedChecklists() {
        return completedChecklists;
    }

    public void setCompletedChecklists(long completedChecklists) {
        this.completedChecklists = completedChecklists;
    }
}
