package com.checkmate.backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    
    @ElementCollection 
    private List<String> assignees;
    
    private String priority;
    private int dueDateDays;
    private String dependsOn;
    private String conditionDependentOn;
    private String conditionExpectedOutcome;
    private int remindBefore;
    private String escalateTo;
    private boolean showAdvanced;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getAssignees() {
		return assignees;
	}
	public void setAssignees(List<String> assignees) {
		this.assignees = assignees;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public int getDueDateDays() {
		return dueDateDays;
	}
	public void setDueDateDays(int dueDateDays) {
		this.dueDateDays = dueDateDays;
	}
	public String getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(String dependsOn) {
		this.dependsOn = dependsOn;
	}
	public String getConditionDependentOn() {
		return conditionDependentOn;
	}
	public void setConditionDependentOn(String conditionDependentOn) {
		this.conditionDependentOn = conditionDependentOn;
	}
	public String getConditionExpectedOutcome() {
		return conditionExpectedOutcome;
	}
	public void setConditionExpectedOutcome(String conditionExpectedOutcome) {
		this.conditionExpectedOutcome = conditionExpectedOutcome;
	}
	public int getRemindBefore() {
		return remindBefore;
	}
	public void setRemindBefore(int remindBefore) {
		this.remindBefore = remindBefore;
	}
	public String getEscalateTo() {
		return escalateTo;
	}
	public void setEscalateTo(String escalateTo) {
		this.escalateTo = escalateTo;
	}
	public boolean isShowAdvanced() {
		return showAdvanced;
	}
	public void setShowAdvanced(boolean showAdvanced) {
		this.showAdvanced = showAdvanced;
	}

}