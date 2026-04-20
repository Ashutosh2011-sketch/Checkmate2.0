package com.checkmate.backend.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnore; // ✅ IMPORTANT

@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_name", nullable = false)
    private String sectionName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    // ✅ FIX: PREVENT INFINITE LOOP
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getSectionName() { return sectionName; }

    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public Integer getSortOrder() { return sortOrder; }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Checklist getChecklist() { return checklist; }

    public void setChecklist(Checklist checklist) { this.checklist = checklist; }

    public List<Task> getTasks() { return tasks; }

    public void setTasks(List<Task> tasks) {
        this.tasks.clear();
        if (tasks == null) return;

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            task.setSection(this);
            task.setSortOrder(i);
            this.tasks.add(task);
        }
    }
}