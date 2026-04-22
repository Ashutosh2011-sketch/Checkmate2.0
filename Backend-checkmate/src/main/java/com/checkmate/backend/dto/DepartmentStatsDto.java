package com.checkmate.backend.dto;

import lombok.Data;

@Data
public class DepartmentStatsDto {
    private Long departmentId;
    private String departmentName;
    private Integer totalChecklists;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer pendingTasks;
    private Integer overdueTasks;
    private Double completionRate;
}