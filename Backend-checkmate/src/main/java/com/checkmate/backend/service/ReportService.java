package com.checkmate.backend.service;

import com.checkmate.backend.dto.*;
import com.checkmate.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ChecklistRepository checklistRepository;

    // Department-wise statistics
    public List<DepartmentStatsDto> getDepartmentStatistics() {
        int currentDayOfYear = LocalDate.now().getDayOfYear();
        List<Object[]> stats = taskRepository.getTaskStatsByDepartment(currentDayOfYear);
        
        List<DepartmentStatsDto> result = new ArrayList<>();
        long deptId = 1L;
        
        for (Object[] row : stats) {
            DepartmentStatsDto dto = new DepartmentStatsDto();
            dto.setDepartmentId(deptId++);
            dto.setDepartmentName((String) row[0]);
            dto.setTotalTasks(((Number) row[1]).intValue());
            dto.setCompletedTasks(((Number) row[2]).intValue());
            dto.setOverdueTasks(((Number) row[3]).intValue());
            dto.setPendingTasks(dto.getTotalTasks() - dto.getCompletedTasks());
            
            List<?> deptChecklists = checklistRepository.findByDepartment(dto.getDepartmentName());
            dto.setTotalChecklists(deptChecklists.size());
            
            dto.setCompletionRate(dto.getTotalTasks() > 0 ? 
                (dto.getCompletedTasks() * 100.0 / dto.getTotalTasks()) : 0.0);
            
            result.add(dto);
        }
        
        return result;
    }

    // Overdue items
    public List<OverdueItemDto> getOverdueItems(String department) {
        int currentDayOfYear = LocalDate.now().getDayOfYear();
        
        List<Object[]> overdueTasks = department != null ?
            taskRepository.findOverdueTasksByDepartment(currentDayOfYear, department) :
            taskRepository.findAllOverdueTasks(currentDayOfYear);

        return overdueTasks.stream().map(row -> {
            OverdueItemDto dto = new OverdueItemDto();
            dto.setTaskId(((Number) row[0]).longValue());
            dto.setTaskName((String) row[1]);
            dto.setPriority((String) row[2]);
            
            int dueDay = ((Number) row[3]).intValue();
            int daysOverdue = currentDayOfYear - dueDay;
            dto.setDaysOverdue(daysOverdue);
            
            dto.setChecklistName((String) row[4]);
            dto.setAssignedTo((String) row[6]);
            dto.setDepartmentName((String) row[7]);
            dto.setDueDate(LocalDate.now());
            
            return dto;
        }).collect(Collectors.toList());
    }

    // User performance
    public List<UserPerformanceDto> getUserPerformance(String department) {
        int currentDayOfYear = LocalDate.now().getDayOfYear();
        List<Object[]> stats = taskRepository.getUserPerformanceStats(currentDayOfYear);
        
        long userId = 1L;
        List<UserPerformanceDto> result = new ArrayList<>();
        
        for (Object[] row : stats) {
            String dept = (String) row[1];
            if (department != null && !department.equals(dept)) continue;
            
            UserPerformanceDto dto = new UserPerformanceDto();
            dto.setUserId(userId++);
            dto.setUserName((String) row[0]);
            dto.setDepartmentName(dept);
            dto.setTotalAssigned(((Number) row[2]).intValue());
            dto.setCompleted(((Number) row[3]).intValue());
            dto.setOverdue(((Number) row[4]).intValue());
            dto.setInProgress(dto.getTotalAssigned() - dto.getCompleted());
            
            dto.setCompletionRate(dto.getTotalAssigned() > 0 ? 
                (dto.getCompleted() * 100.0 / dto.getTotalAssigned()) : 0.0);
            
            dto.setAvgCompletionDays(0.0);
            
            result.add(dto);
        }
        
        return result.stream()
            .sorted((a, b) -> Double.compare(b.getCompletionRate(), a.getCompletionRate()))
            .collect(Collectors.toList());
    }

    // Completion trends
    public List<CompletionTrendDto> getCompletionTrends(LocalDate startDate, LocalDate endDate, String groupBy) {
        List<Object[]> trendData = "WEEK".equalsIgnoreCase(groupBy) ?
            checklistRepository.getWeeklyCompletionTrend(startDate, endDate) :
            checklistRepository.getMonthlyCompletionTrend(startDate, endDate);

        return trendData.stream().map(row -> {
            CompletionTrendDto dto = new CompletionTrendDto();
            dto.setPeriod((String) row[0]);
            dto.setCompleted(((Number) row[1]).intValue());
            dto.setTotal(((Number) row[2]).intValue());
            dto.setCompletionRate(dto.getTotal() > 0 ? 
                (dto.getCompleted() * 100.0 / dto.getTotal()) : 0.0);
            return dto;
        }).collect(Collectors.toList());
    }

    // Bottleneck analysis
    public List<BottleneckDto> getBottleneckAnalysis() {
        List<Object[]> data = checklistRepository.findBottlenecks();
        
        return data.stream().map(row -> {
            BottleneckDto dto = new BottleneckDto();
            dto.setChecklistId(((Number) row[0]).longValue());
            dto.setChecklistName((String) row[1]);
            dto.setCurrentLevel((String) row[2]);
            dto.setPendingTasksCount(((Number) row[3]).intValue());
            dto.setDaysStuck(((Number) row[4]).intValue());
            dto.setDepartmentName((String) row[5]);
            return dto;
        }).collect(Collectors.toList());
    }
}