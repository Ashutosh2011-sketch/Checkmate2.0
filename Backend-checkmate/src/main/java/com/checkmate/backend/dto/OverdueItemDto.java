package com.checkmate.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OverdueItemDto {
    private Long taskId;
    private String taskName;
    private String checklistName;
    private String assignedTo;
    private LocalDate dueDate;
    private Integer daysOverdue;
    private String departmentName;
    private String priority;
}