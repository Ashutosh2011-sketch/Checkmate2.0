package com.checkmate.backend.repository;

import com.checkmate.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {
	
	

    // âœ… EXISTING
    @Query(value = """
        SELECT t.title
        FROM tasks t
        JOIN task_assignees ta ON t.id = ta.task_id
        WHERE ta.assignee ILIKE CONCAT('%', :userName, '%')
    """, nativeQuery = true)
    List<String> findTasksByUserName(@Param("userName") String userName);
    
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
    	""", nativeQuery = true)
    	List<Object[]> findOverdueTasksByDepartment(
    	        @Param("currentDayOfYear") int currentDayOfYear,
    	        @Param("department") String department
    	);

    // âœ… EXISTING
    @Query(value = """
        SELECT t.id, t.title, t.priority, t.description,
               s.section_name, c.checklist_name, c.id as checklist_id
        FROM tasks t
        JOIN task_assignees ta ON t.id = ta.task_id
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        WHERE ta.assignee ILIKE CONCAT('%', :userName, '%')
    """, nativeQuery = true)
    List<Object[]> findFullTasksByUserName(@Param("userName") String userName);

    // ðŸ”¥ NEW â†’ THIS FIXES YOUR USER TASK API (USE THIS IN CONTROLLER)
    @Query(value = """
    	    SELECT 
    	        t.id,
    	        t.title,
    	        t.description,
    	        t.priority,
    	        t.due_date_days,
    	        t.completed,
    	        s.section_name,
    	        COALESCE(STRING_AGG(DISTINCT ta.assignee, ','), '') as assignees,
    	        t.depends_on,
    	        c.id as checklist_id,
    	        c.checklist_name,
    	        c.department,
    	        c.visibility,
    	        c.workflow_type,
    	        t.condition_dependent_on,
    	        t.condition_expected_outcome,
    	        t.sort_order
    	    FROM tasks t
    	    JOIN sections s ON t.section_id = s.id
    	    JOIN checklists c ON s.checklist_id = c.id
    	    LEFT JOIN task_assignees ta ON ta.task_id = t.id
    	    WHERE EXISTS (
    	        SELECT 1
    	        FROM task_assignees current_user_assignee
    	        WHERE current_user_assignee.task_id = t.id
    	          AND LOWER(TRIM(SPLIT_PART(current_user_assignee.assignee, '(', 1))) = LOWER(TRIM(:username))
    	    )
    	    GROUP BY 
    	        t.id,
	        s.id,
    	        t.title,
    	        t.description,
    	        t.priority,
    	        t.due_date_days,
    	        t.completed,
    	        s.section_name,
    	        t.depends_on,
    	        c.id,
    	        c.checklist_name,
    	        c.department,
    	        c.visibility,
    	        c.workflow_type,
    	        t.condition_dependent_on,
    	        t.condition_expected_outcome,
    	        t.sort_order
    	    ORDER BY c.id, s.id, t.sort_order, t.id
    	""", nativeQuery = true)
    	List<Object[]> findTasksByExactUser(@Param("username") String username);


    // âœ… EXISTING
    @Query(value = """
        SELECT COUNT(t.id)
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        WHERE s.checklist_id = :checklistId
    """, nativeQuery = true)
    int countTasksByChecklistId(@Param("checklistId") Long checklistId);

    // âœ… EXISTING
    @Query(value = """
        SELECT COUNT(t.id)
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        WHERE c.completed = true
    """, nativeQuery = true)
    long countCompletedTasks();

    // ðŸ“Š Total tasks
    @Query(value = "SELECT COUNT(*) FROM tasks", nativeQuery = true)
    long countTotalTasks();

    // ðŸ“Š Pending tasks
    @Query(value = """
        SELECT COUNT(*) FROM tasks t
        WHERE t.completed = false
    """, nativeQuery = true)
    long countPendingTasks();

    // â° Overdue
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

    // ðŸ‘¤ Tasks assigned
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

    // ðŸ“Š Department stats
    @Query(value = """
        SELECT 
            COALESCE(c.department, 'Unassigned') as department,
            COUNT(DISTINCT t.id),
            COUNT(DISTINCT CASE WHEN t.completed = true THEN t.id END),
            COUNT(DISTINCT CASE WHEN t.completed = false AND t.due_date_days < :currentDayOfYear THEN t.id END)
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        GROUP BY c.department
    """, nativeQuery = true)
    List<Object[]> getTaskStatsByDepartment(@Param("currentDayOfYear") int currentDayOfYear);

    // ðŸ‘¤ User performance
    @Query(value = """
        SELECT 
            ta.assignee,
            COALESCE(c.department, 'Unassigned'),
            COUNT(DISTINCT t.id),
            COUNT(DISTINCT CASE WHEN t.completed = true THEN t.id END),
            COUNT(DISTINCT CASE WHEN t.completed = false AND t.due_date_days < :currentDayOfYear THEN t.id END)
        FROM task_assignees ta
        JOIN tasks t ON ta.task_id = t.id
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        GROUP BY ta.assignee, c.department
        ORDER BY COUNT(DISTINCT t.id) DESC
    """, nativeQuery = true)
    List<Object[]> getUserPerformanceStats(@Param("currentDayOfYear") int currentDayOfYear);

    // âœ… CHECKLIST DETAIL PAGE
    @Query("""
        SELECT t FROM Task t
        JOIN FETCH t.section s
        JOIN FETCH s.checklist c
        WHERE c.id = :checklistId
    """)
    List<Task> findBySection_Checklist_Id(@Param("checklistId") Long checklistId);
    
    
}
