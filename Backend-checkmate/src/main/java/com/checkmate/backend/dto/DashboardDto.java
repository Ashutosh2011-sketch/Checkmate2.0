package com.checkmate.backend.dto;

import java.util.List;

public class DashboardDto {

    private List<String> assignedChecklists; // rename
    private List<String> claimedTasks;
    private List<String> notifications;

    private int progress;

    // GETTERS & SETTERS

    public List<String> getAssignedChecklists() {
        return assignedChecklists;
    }

    public void setAssignedChecklists(List<String> assignedChecklists) {
        this.assignedChecklists = assignedChecklists;
    }

    public List<String> getClaimedTasks() {
        return claimedTasks;
    }

    public void setClaimedTasks(List<String> claimedTasks) {
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