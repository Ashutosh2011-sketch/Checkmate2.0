package com.checkmate.backend.controller;

import com.checkmate.backend.entity.Task;
import com.checkmate.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // ✅ GET TASKS BY CHECKLIST
    @GetMapping("/checklist/{checklistId}")
    public List<TaskResponse> getTasksByChecklist(@PathVariable Long checklistId) {

        List<Task> tasks = taskRepository.findBySection_Checklist_Id(checklistId);

        return tasks.stream().map(task -> {
            TaskResponse dto = new TaskResponse();

            dto.id = task.getId();
            dto.title = task.getTitle();
            dto.description = task.getDescription();
            dto.priority = task.getPriority();
            dto.dueDateDays = task.getDueDateDays();
            dto.status = task.isCompleted() ? "Completed" : "Pending";

            dto.sectionName = task.getSection() != null
                    ? task.getSection().getSectionName()
                    : "General";

            dto.assignees = task.getAssignees() != null
                    ? task.getAssignees()
                    : List.of();

            dto.dependsOn = task.getDependsOn();
            dto.conditionDependentOn = task.getConditionDependentOn();
            dto.conditionExpectedOutcome = task.getConditionExpectedOutcome();
            dto.sortOrder = task.getSortOrder();
            dto.workflowType = "SEQUENTIAL";
            dto.completedAt = task.getCompletedAt();
            dto.completedBy = task.getCompletedBy();

            return dto;
        }).collect(Collectors.toList());
    }

    // ✅ GET TASKS BY USER
    @GetMapping("/user/{username}")
    public ResponseEntity<List<TaskResponse>> getTasksByUser(@PathVariable String username) {

        List<Object[]> rows = taskRepository.findTasksByExactUser(username);

        List<TaskResponse> response = rows.stream().map(row -> {
            TaskResponse dto = new TaskResponse();

            dto.id = ((Number) row[0]).longValue();
            dto.title = (String) row[1];
            dto.description = (String) row[2];
            dto.priority = (String) row[3];
            dto.dueDateDays = ((Number) row[4]).intValue();
            dto.status = ((Boolean) row[5]) ? "Completed" : "Pending";
            dto.sectionName = (String) row[6];
            dto.assignees = List.of(((String) row[7]).split(","));

            dto.workflowType = "SEQUENTIAL";

         // 🔥 ADD THIS LINE (MAIN FIX)
         dto.dependsOn = row[8] != null ? String.valueOf(row[8]) : null;

            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // 🔥 TOGGLE TASK (MAIN FIX)
    @PutMapping("/toggle/{taskId}")
    public Task toggleTask(@PathVariable Long taskId, Principal principal) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean newStatus = !task.isCompleted();

        task.setCompleted(newStatus);
        task.setCompletionPercent(newStatus ? 100 : 0);
        task.setStatus(newStatus ? "Completed" : "Pending");
        if (newStatus) {
            task.setCompletedAt(LocalDateTime.now());
            if (principal != null) {
                task.setCompletedBy(principal.getName());
            }
        } else {
            task.setCompletedAt(null);
            task.setCompletedBy(null);
        }

        return taskRepository.save(task);
    }

    // 📦 DTO
    static class TaskResponse {
        public Long id;
        public String title;
        public String description;
        public String priority;
        public int dueDateDays;
        public String status;
        public String sectionName;
        public List<String> assignees;

        public String dependsOn;
        public String conditionDependentOn;
        public String conditionExpectedOutcome;
        public Integer sortOrder;
        public String workflowType;
        public LocalDateTime completedAt;
        public String completedBy;

        public TaskResponse() {}
    }
}