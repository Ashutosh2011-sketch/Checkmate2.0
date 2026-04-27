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

    @Column(name = "status")
    private String status;

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
        return status != null ? status : "Pending";
    }
    public void setStatus(String status) { this.status = status; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }
}