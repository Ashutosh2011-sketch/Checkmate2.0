package com.checkmate.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PermissionDto {

    private Long id;
    private String name;
    private String category;

    @JsonProperty("isEnabled")
    private boolean isEnabled;

    // ===== GETTERS & SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @JsonProperty("isEnabled")
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
}
