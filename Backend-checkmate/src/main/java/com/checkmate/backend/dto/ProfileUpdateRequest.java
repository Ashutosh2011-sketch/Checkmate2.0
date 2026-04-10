package com.checkmate.backend.dto;

public class ProfileUpdateRequest {
    private String fullName;
    private String jobTitle;
    private String department;
    private String currentPassword;
    private String newPassword;

    public ProfileUpdateRequest() {
    }

    public ProfileUpdateRequest(String fullName, String jobTitle, String department, String currentPassword,
            String newPassword) {
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.department = department;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
