package com.checkmate.backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_assignees", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "assignee")
    private List<String> assignees = new ArrayList<>();

    @Column(nullable = false)
    private String priority;

    @Column(name = "due_date_days", nullable = false)
    private int dueDateDays;

    @Column(name = "status", nullable = false)
    private String status = "Pending";

    // ✅ KEEP STRING (DO NOT CHANGE TYPE)
    @Column(name = "depends_on")
    private String dependsOn;

    @Column(name = "condition_dependent_on")
    private String conditionDependentOn;

    @Column(name = "condition_expected_outcome")
    private String conditionExpectedOutcome;

    @Column(name = "remind_before")
    private Integer remindBefore = 1;

    @Column(name = "escalate_to")
    private String escalateTo = "Manager";

    @Column(name = "show_advanced")
    private boolean showAdvanced = false;

    @Column(name = "completion_percent")
    private Integer completionPercent = 0;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnore
    private Section section;

    // ---------------- GETTERS ----------------

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public List<String> getAssignees() { return assignees; }

    public String getPriority() { return priority; }

    public int getDueDateDays() { return dueDateDays; }

    public String getStatus() {
        if (Boolean.TRUE.equals(completed)) return "Completed";
        if (completionPercent != null && completionPercent > 0) return "In Progress";
        return status != null ? status : "Pending";
    }

    public boolean isCompleted() { return Boolean.TRUE.equals(completed); }

    public Integer getSortOrder() { return sortOrder; }

    public Section getSection() { return section; }

    public String getDependsOn() { return dependsOn; }

    public String getConditionDependentOn() { return conditionDependentOn; }

    public String getConditionExpectedOutcome() { return conditionExpectedOutcome; }

    public Integer getRemindBefore() { return remindBefore; }

    public String getEscalateTo() { return escalateTo; }

    public boolean isShowAdvanced() { return showAdvanced; }

    public Integer getCompletionPercent() { return completionPercent; }

    // ---------------- SETTERS ----------------

    public void setId(Long id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setDescription(String description) { this.description = description; }

    public void setAssignees(List<String> assignees) { this.assignees = assignees; }

    public void setPriority(String priority) { this.priority = priority; }

    public void setDueDateDays(int dueDateDays) { this.dueDateDays = dueDateDays; }

    public void setStatus(String status) { this.status = status; }

    public void setDependsOn(String dependsOn) { this.dependsOn = dependsOn; }

    public void setConditionDependentOn(String conditionDependentOn) {
        this.conditionDependentOn = conditionDependentOn;
    }

    public void setConditionExpectedOutcome(String conditionExpectedOutcome) {
        this.conditionExpectedOutcome = conditionExpectedOutcome;
    }

    public void setRemindBefore(Integer remindBefore) { this.remindBefore = remindBefore; }

    public void setEscalateTo(String escalateTo) { this.escalateTo = escalateTo; }

    public void setShowAdvanced(boolean showAdvanced) { this.showAdvanced = showAdvanced; }

    public void setCompletionPercent(Integer completionPercent) {
        this.completionPercent = completionPercent;
    }

    public void setCompleted(Boolean completed) { this.completed = completed; }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public void setSection(Section section) { this.section = section; }
}