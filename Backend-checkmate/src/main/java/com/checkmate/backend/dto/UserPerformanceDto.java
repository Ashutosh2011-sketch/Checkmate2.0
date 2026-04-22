package com.checkmate.backend.dto;

import lombok.Data;

@Data
public class UserPerformanceDto {
    private Long userId;
    private String userName;
    private String departmentName;
    private Integer totalAssigned;
    private Integer completed;
    private Integer inProgress;
    private Integer overdue;
    private Double completionRate;
    private Double avgCompletionDays;
}