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

    @Column(name = "depends_on", nullable = false)
    private String dependsOn = "None";

    @Column(name = "condition_dependent_on", nullable = false)
    private String conditionDependentOn = "None";

    @Column(name = "condition_expected_outcome", nullable = false)
    private String conditionExpectedOutcome = "Pass";

    @Column(name = "remind_before", nullable = false)
    private Integer remindBefore = 1;

    @Column(name = "escalate_to", nullable = false)
    private String escalateTo = "Manager";

    @Column(name = "show_advanced", nullable = false)
    private boolean showAdvanced = false;

    @Column(name = "completion_percent")
    private int completionPercent = 0;

    @Column(name = "completed")
    private boolean completed = false;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnore
    private Section section;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getAssignees() { return assignees; }
    public void setAssignees(List<String> assignees) { this.assignees = assignees; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public int getDueDateDays() { return dueDateDays; }
    public void setDueDateDays(int dueDateDays) { this.dueDateDays = dueDateDays; }

    public String getStatus() {
        if (completed) return "Completed";
        if (completionPercent > 0) return "In Progress";
        return status != null ? status : "Pending";
    }
    public void setStatus(String status) { this.status = status != null ? status : "Pending"; }

    public String getDependsOn() { return dependsOn; }
    public void setDependsOn(String dependsOn) { this.dependsOn = dependsOn != null ? dependsOn : "None"; }

    public String getConditionDependentOn() { return conditionDependentOn; }
    public void setConditionDependentOn(String conditionDependentOn) {
        this.conditionDependentOn = conditionDependentOn != null ? conditionDependentOn : "None";
    }

    public String getConditionExpectedOutcome() { return conditionExpectedOutcome; }
    public void setConditionExpectedOutcome(String conditionExpectedOutcome) {
        this.conditionExpectedOutcome = conditionExpectedOutcome != null ? conditionExpectedOutcome : "Pass";
    }

    public Integer getRemindBefore() { return remindBefore != null ? remindBefore : 1; }
    public void setRemindBefore(Integer remindBefore) { this.remindBefore = remindBefore != null ? remindBefore : 1; }

    public String getEscalateTo() { return escalateTo; }
    public void setEscalateTo(String escalateTo) { this.escalateTo = escalateTo != null ? escalateTo : "Manager"; }

    public boolean isShowAdvanced() { return showAdvanced; }
    public void setShowAdvanced(boolean showAdvanced) { this.showAdvanced = showAdvanced; }

    public int getCompletionPercent() { return completionPercent; }
    public void setCompletionPercent(int completionPercent) { this.completionPercent = completionPercent; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }
}