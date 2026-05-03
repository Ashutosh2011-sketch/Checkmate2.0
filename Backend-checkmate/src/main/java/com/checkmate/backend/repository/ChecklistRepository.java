package com.checkmate.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.checkmate.backend.dto.ChecklistSummaryDto;
import com.checkmate.backend.entity.Checklist;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    // ✅ Existing method
    long countByCompletedTrue();
    
    // ✅ Existing method
    @Query(value = "SELECT " +
           "c.id, " +
           "c.checklist_name as title, " +
           "COALESCE(STRING_AGG(DISTINCT ta.assignee, ', '), 'Unassigned') as assignee, " +
           "CASE WHEN c.completed THEN 'Completed' ELSE 'Pending' END as status, " +
           "MAX(t.due_date_days) as due_date_days, " +
           "MAX(t.priority) as priority " +
           "FROM checklists c " +
           "LEFT JOIN sections s ON s.checklist_id = c.id " +
           "LEFT JOIN tasks t ON t.section_id = s.id " +
           "LEFT JOIN task_assignees ta ON ta.task_id = t.id " +
           "GROUP BY c.id, c.checklist_name, c.completed", nativeQuery = true)
    List<ChecklistSummaryDto> findAllSummaries();

    // ─────────────────────────────────────────────────────────────────────────
    // 🆕 NEW METHODS FOR REPORTS
    // ─────────────────────────────────────────────────────────────────────────

    // 📊 Monthly Completion Trends
    @Query(value = """
    SELECT 
        TO_CHAR(DATE_TRUNC('month', c.created_at), 'Mon YYYY') as period,
        COUNT(*) as completed_count,
        COUNT(*) as total_completed
    FROM checklists c
    WHERE c.created_at BETWEEN :startDate AND :endDate
    GROUP BY DATE_TRUNC('month', c.created_at)
    ORDER BY DATE_TRUNC('month', c.created_at)
""", nativeQuery = true)
List<Object[]> getMonthlyCompletionTrend(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);

    // 📊 Weekly Completion Trends
   @Query(value = """
    SELECT 
        CONCAT('Week ', TO_CHAR(DATE_TRUNC('week', c.created_at), 'IW')) as period,
        COUNT(*) as completed_count,
        COUNT(*) as total_completed
    FROM checklists c
    WHERE c.created_at BETWEEN :startDate AND :endDate
    GROUP BY DATE_TRUNC('week', c.created_at)
    ORDER BY DATE_TRUNC('week', c.created_at)
""", nativeQuery = true)
List<Object[]> getWeeklyCompletionTrend(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);

    // 🚨 Bottleneck Analysis - Checklists stuck in progress
    @Query(value = """
    SELECT 
        c.id,
        c.checklist_name,
        'In Progress' as current_level,
        COUNT(DISTINCT t.id) as pending_tasks,
       COALESCE(EXTRACT(DAY FROM (NOW() - c.created_at)), 0) as days_stuck,
        COALESCE(c.department, 'Unassigned') as department_name
    FROM checklists c
    JOIN sections s ON s.checklist_id = c.id
    JOIN tasks t ON t.section_id = s.id
    WHERE c.completed = false 
    AND t.completed = false
    GROUP BY c.id, c.checklist_name, c.updated_at, c.department
    ORDER BY days_stuck DESC
    """, nativeQuery = true)
List<Object[]> findBottlenecks();

        // 📋 Find checklists by department
        @Query(value = """
            SELECT * FROM checklists c
            WHERE c.department = :department
        """, nativeQuery = true)
        List<Checklist> findByDepartment(@Param("department") String department);
    }