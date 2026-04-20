package com.checkmate.backend.repository;

import com.checkmate.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // ✅ FIXED METHOD (this was missing)
    @Query(value = """
        SELECT t.title
        FROM tasks t
        JOIN task_assignees ta ON t.id = ta.task_id
        WHERE ta.assignee LIKE CONCAT(:userName, '%')
    """, nativeQuery = true)
    List<String> findTasksByUserName(String userName);

<<<<<<< HEAD
    // Get full task objects assigned to a user
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

    // Count total tasks in a checklist
    @Query(value = """
        SELECT COUNT(t.id)
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        WHERE s.checklist_id = :checklistId
    """, nativeQuery = true)
    int countTasksByChecklistId(@Param("checklistId") Long checklistId);

    @Query(value = """
        SELECT COUNT(t.id)
        FROM tasks t
        JOIN sections s ON t.section_id = s.id
        JOIN checklists c ON s.checklist_id = c.id
        WHERE c.completed = true
    """, nativeQuery = true)
    long countCompletedTasks();
=======

    // ✅ FOR CHECKLIST DETAIL PAGE (already needed)
    @Query("SELECT t FROM Task t JOIN FETCH t.section s JOIN FETCH s.checklist WHERE s.checklist.id = :checklistId")
    List<Task> findBySection_Checklist_Id(Long checklistId);
>>>>>>> 5cd979d (Checklist details page is updated)
}