package com.checkmate.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class ChecklistDto {
    private Long id;
    private String checklistName;
    private String department;
    private String visibility;
    private String workflowType;
    private boolean completed;
    private List<SectionDto> sections = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getChecklistName() { return checklistName; }
    public void setChecklistName(String checklistName) { this.checklistName = checklistName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public String getWorkflowType() { return workflowType; }
    public void setWorkflowType(String workflowType) { this.workflowType = workflowType; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public List<SectionDto> getSections() { return sections; }
    public void setSections(List<SectionDto> sections) { this.sections = sections == null ? new ArrayList<>() : sections; }
}

