package com.checkmate.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TemplateDto {
    private Long id;
    private String templateName;
    private String department;
    private String visibility;
    private String workflowType;
    private String description;
    private String createdBy;
    private Integer currentVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TemplateSectionDto> sections = new ArrayList<>();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public String getWorkflowType() { return workflowType; }
    public void setWorkflowType(String workflowType) { this.workflowType = workflowType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Integer getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Integer currentVersion) { this.currentVersion = currentVersion; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<TemplateSectionDto> getSections() { return sections; }
    public void setSections(List<TemplateSectionDto> sections) { this.sections = sections == null ? new ArrayList<>() : sections; }

    // ── Nested DTOs ──────────────────────────────────────────────────────────

    public static class TemplateSectionDto {
        private String sectionName;
        private List<TemplateTaskDto> tasks = new ArrayList<>();
        public String getSectionName() { return sectionName; }
        public void setSectionName(String sectionName) { this.sectionName = sectionName; }
        public List<TemplateTaskDto> getTasks() { return tasks; }
        public void setTasks(List<TemplateTaskDto> tasks) { this.tasks = tasks == null ? new ArrayList<>() : tasks; }
    }

    public static class TemplateTaskDto {
        private String title;
        private String description;
        private String priority;
        private int dueDateDays;
        private List<String> assignees = new ArrayList<>();
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public int getDueDateDays() { return dueDateDays; }
        public void setDueDateDays(int dueDateDays) { this.dueDateDays = dueDateDays; }
        public List<String> getAssignees() { return assignees; }
        public void setAssignees(List<String> assignees) { this.assignees = assignees == null ? new ArrayList<>() : assignees; }
    }
}