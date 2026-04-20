package com.checkmate.backend.dto;

import java.util.List;

public class UserPermissionDto {

    private Long userId;
    private String userName;
    private String email;
    private String designation;
    private List<PermissionDto> permissions;

    public UserPermissionDto() {}

    public UserPermissionDto(Long userId, String userName, String email, String designation) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.designation = designation;
    }

    // ===== GETTERS & SETTERS =====

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public List<PermissionDto> getPermissions() { return permissions; }
    public void setPermissions(List<PermissionDto> permissions) { this.permissions = permissions; }
}
