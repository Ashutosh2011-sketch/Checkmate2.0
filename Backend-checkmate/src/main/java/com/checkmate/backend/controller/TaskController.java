package com.checkmate.backend.controller;

import com.checkmate.backend.entity.Task;
import com.checkmate.backend.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        return tasks.stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList());
    }

    // ✅ GET TASKS BY USER
 // ✅ GET TASKS VISIBLE TO USER
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

            String assigneeValue = row[7] != null ? (String) row[7] : "";
            dto.assignees = assigneeValue.isBlank()
                    ? List.of()
                    : List.of(assigneeValue.split(","));

            dto.dependsOn = row[8] != null ? String.valueOf(row[8]) : null;
            dto.checklistId = row[9] != null ? ((Number) row[9]).longValue() : null;
            dto.checklistName = row[10] != null ? (String) row[10] : "";
            dto.department = row[11] != null ? (String) row[11] : "";
            dto.visibility = row[12] != null ? (String) row[12] : "";
            dto.workflowType = row[13] != null ? (String) row[13] : "Sequential";
            dto.conditionDependentOn = row[14] != null ? String.valueOf(row[14]) : null;
            dto.conditionExpectedOutcome = row[15] != null ? String.valueOf(row[15]) : null;
            dto.sortOrder = row[16] != null ? ((Number) row[16]).intValue() : 0;

            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/claim")
    @Transactional
    public ResponseEntity<?> claimTask(@PathVariable Long taskId, @RequestBody Map<String, String> body) {

        String userName = body.getOrDefault("userName", "").trim();
        if (userName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User name is required"));
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        String visibility = task.getSection() != null && task.getSection().getChecklist() != null
                ? task.getSection().getChecklist().getVisibility()
                : "";

        if (!"Public".equalsIgnoreCase(visibility)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only public checklist tasks can be claimed"));
        }

        if (!isUnassigned(task)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Task is already assigned"));
        }

        List<String> assignees = new ArrayList<>();
        assignees.add(userName);
        task.setAssignees(assignees);

        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(toTaskResponse(savedTask));
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
    private TaskResponse toTaskResponse(Task task) {
        TaskResponse dto = new TaskResponse();

        dto.id = task.getId();
        dto.title = task.getTitle();
        dto.description = task.getDescription();
        dto.priority = task.getPriority();
        dto.dueDateDays = task.getDueDateDays();
        dto.status = task.getStatus();

        dto.sectionName = task.getSection() != null
                ? task.getSection().getSectionName()
                : "General";

        dto.assignees = task.getAssignees() != null
                ? task.getAssignees()
                : List.of();

        if (task.getSection() != null && task.getSection().getChecklist() != null) {
            dto.checklistId = task.getSection().getChecklist().getId();
            dto.checklistName = task.getSection().getChecklist().getChecklistName();
            dto.department = task.getSection().getChecklist().getDepartment();
            dto.visibility = task.getSection().getChecklist().getVisibility();
            dto.workflowType = task.getSection().getChecklist().getWorkflowType();
        }

        dto.dependsOn = task.getDependsOn();
        dto.conditionDependentOn = task.getConditionDependentOn();
        dto.conditionExpectedOutcome = task.getConditionExpectedOutcome();
        dto.sortOrder = task.getSortOrder();
        dto.completedAt = task.getCompletedAt();
        dto.completedBy = task.getCompletedBy();

        return dto;
    }

    private boolean isUnassigned(Task task) {
        return task.getAssignees() == null || task.getAssignees().stream()
                .map(assignee -> assignee == null ? "" : assignee.trim())
                .allMatch(assignee -> assignee.isBlank() || "Unassigned".equalsIgnoreCase(assignee));
    }

    static class TaskResponse {
        public Long id;
        public String title;
        public String description;
        public String priority;
        public int dueDateDays;
        public String status;
        public String sectionName;
        public List<String> assignees;
        public Long checklistId;
        public String checklistName;
        public String department;
        public String visibility;
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
