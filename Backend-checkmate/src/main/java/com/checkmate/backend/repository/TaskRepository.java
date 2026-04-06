package com.checkmate.backend.repository;

import com.checkmate.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = """
        SELECT t.title
        FROM tasks t
        JOIN task_assignees ta ON t.id = ta.task_id
        WHERE ta.assignee LIKE CONCAT(:userName, '%')
    """, nativeQuery = true)
    List<String> findTasksByUserName(String userName);
}