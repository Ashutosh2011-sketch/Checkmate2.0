package com.checkmate.backend.repository;

import com.checkmate.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // ✅ Existing method
    @Query(value = """
        SELECT t.title
        FROM tasks t
        JOIN task_assignees ta ON t.id = ta.task_id
        WHERE ta.assignee LIKE CONCAT(:userName, '%')
    """, nativeQuery = true)
    List<String> findTasksByUserName(@Param("userName") String userName);

    // ✅ Existing method
    @Query(value = """
        SELECT t.id, t.title, t.priority, t.description,
               s.section_name, c.checklist_name, c.id as checklist_id
        FROM tasks t
        JOIN task_assignees ta ON t.id = ta.task_id
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        WHERE ta.assignee LIKE CONCAT(:userName, '%')
    """, nativeQuery = true)
    List<Object[]> findFullTasksByUserName(@Param("userName") String userName);

    // ✅ Existing method
    @Query(value = """
        SELECT COUNT(t.id)
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        WHERE s.checklist_id = :checklistId
    """, nativeQuery = true)
    int countTasksByChecklistId(@Param("checklistId") Long checklistId);

    // ✅ Existing method
    @Query(value = """
        SELECT COUNT(t.id)
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        WHERE c.completed = true
    """, nativeQuery = true)
    long countCompletedTasks();

    // ─────────────────────────────────────────────────────────────────────────
    // 🆕 NEW METHODS FOR REPORTS
    // ─────────────────────────────────────────────────────────────────────────

    // 📊 Total tasks count
    @Query(value = "SELECT COUNT(*) FROM tasks", nativeQuery = true)
    long countTotalTasks();

    // 📊 Pending tasks count
    @Query(value = """
        SELECT COUNT(*) FROM tasks t
        WHERE t.completed = false
    """, nativeQuery = true)
    long countPendingTasks();

    // ⏰ All overdue tasks
    @Query(value = """
        SELECT 
            t.id, t.title, t.priority, t.due_date_days,
            c.checklist_name, c.id as checklist_id,
            STRING_AGG(DISTINCT ta.assignee, ', ') as assignees,
            COALESCE(c.department, 'Unassigned') as department
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        LEFT JOIN task_assignees ta ON ta.task_id = t.id
        WHERE t.completed = false 
        AND t.due_date_days < :currentDayOfYear
        GROUP BY t.id, t.title, t.priority, t.due_date_days, c.checklist_name, c.id, c.department
        ORDER BY t.due_date_days ASC
    """, nativeQuery = true)
    List<Object[]> findAllOverdueTasks(@Param("currentDayOfYear") int currentDayOfYear);

    // ⏰ Overdue tasks by department
    @Query(value = """
        SELECT 
            t.id, t.title, t.priority, t.due_date_days,
            c.checklist_name, c.id as checklist_id,
            STRING_AGG(DISTINCT ta.assignee, ', ') as assignees,
            COALESCE(c.department, 'Unassigned') as department
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        LEFT JOIN task_assignees ta ON ta.task_id = t.id
        WHERE t.completed = false 
        AND t.due_date_days < :currentDayOfYear
        AND c.department = :department
        GROUP BY t.id, t.title, t.priority, t.due_date_days, c.checklist_name, c.id, c.department
        ORDER BY t.due_date_days ASC
    """, nativeQuery = true)
    List<Object[]> findOverdueTasksByDepartment(@Param("currentDayOfYear") int currentDayOfYear, 
                                                 @Param("department") String department);

    // 👤 Tasks assigned to specific user
    @Query(value = """
        SELECT 
            t.id, t.title, t.priority, t.completed,
            c.checklist_name, c.department
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        JOIN task_assignees ta ON ta.task_id = t.id
        WHERE ta.assignee = :userName
    """, nativeQuery = true)
    List<Object[]> findTasksByAssignee(@Param("userName") String userName);

    // 📊 Tasks by department (for stats)
    @Query(value = """
        SELECT 
            COALESCE(c.department, 'Unassigned') as department,
            COUNT(DISTINCT t.id) as total_tasks,
            COUNT(DISTINCT CASE WHEN t.completed = true THEN t.id END) as completed_tasks,
            COUNT(DISTINCT CASE WHEN t.completed = false AND t.due_date_days < :currentDayOfYear THEN t.id END) as overdue_tasks
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        GROUP BY c.department
    """, nativeQuery = true)
    List<Object[]> getTaskStatsByDepartment(@Param("currentDayOfYear") int currentDayOfYear);

    // 👤 User performance stats
    @Query(value = """
        SELECT 
            ta.assignee as user_name,
            COALESCE(c.department, 'Unassigned') as department,
            COUNT(DISTINCT t.id) as total_assigned,
            COUNT(DISTINCT CASE WHEN t.completed = true THEN t.id END) as completed,
            COUNT(DISTINCT CASE WHEN t.completed = false AND t.due_date_days < :currentDayOfYear THEN t.id END) as overdue
        FROM task_assignees ta
        JOIN tasks t ON ta.task_id = t.id
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        GROUP BY ta.assignee, c.department
        ORDER BY completed DESC
    """, nativeQuery = true)
    List<Object[]> getUserPerformanceStats(@Param("currentDayOfYear") int currentDayOfYear);
}