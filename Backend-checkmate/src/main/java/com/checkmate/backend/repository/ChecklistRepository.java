package com.checkmate.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.checkmate.backend.dto.ChecklistSummaryDto;
import com.checkmate.backend.entity.Checklist;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    
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
}
