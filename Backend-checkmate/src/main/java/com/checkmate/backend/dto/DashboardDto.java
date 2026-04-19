package com.checkmate.backend.dto;

import java.util.List;

public class DashboardDto {

    private List<ChecklistInfoDto> assignedChecklists;
    private List<TaskInfoDto> claimedTasks;
    private List<String> notifications;
    private int progress;

    // GETTERS & SETTERS

    public List<ChecklistInfoDto> getAssignedChecklists() {
        return assignedChecklists;
    }

    public void setAssignedChecklists(List<ChecklistInfoDto> assignedChecklists) {
        this.assignedChecklists = assignedChecklists;
    }

    public List<TaskInfoDto> getClaimedTasks() {
        return claimedTasks;
    }

    public void setClaimedTasks(List<TaskInfoDto> claimedTasks) {
        this.claimedTasks = claimedTasks;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}