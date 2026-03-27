package com.checkmate.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class SectionDto {
    private Long id;
    private String sectionName;
    private List<TaskDto> tasks = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }
    public List<TaskDto> getTasks() { return tasks; }
    public void setTasks(List<TaskDto> tasks) { this.tasks = tasks == null ? new ArrayList<>() : tasks; }
}

