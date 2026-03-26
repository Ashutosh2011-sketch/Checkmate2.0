package com.checkmate.backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Checklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String checklistName;
    private String department;
    private String visibility;
    private String workflowType;

    // ADD THIS FIELD to fix the "NOT NULL" error
    @Column(nullable = false)
    private boolean completed = false; 

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "checklist_id")
    private List<Section> sections;

    // Getters and Setters
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
    
    // Getter and Setter for the NEW field
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
}