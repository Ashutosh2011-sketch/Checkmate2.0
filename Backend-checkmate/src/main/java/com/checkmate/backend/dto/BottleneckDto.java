 package com.checkmate.backend.dto;

import lombok.Data;

@Data
public class BottleneckDto{
    private Long checklistId;
    private String checklistName;
    private String currentLevel;
    private Integer pendingTasksCount;
    private Integer daysStuck;
    private String departmentName;
} 
    

