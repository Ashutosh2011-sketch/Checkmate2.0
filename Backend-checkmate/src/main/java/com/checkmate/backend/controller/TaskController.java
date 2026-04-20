package com.checkmate.backend.controller;

import com.checkmate.backend.entity.Task;
import com.checkmate.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/checklist/{checklistId}")
    public List<TaskResponse> getTasksByChecklist(@PathVariable Long checklistId) {

        List<Task> tasks = taskRepository.findBySection_Checklist_Id(checklistId);

        return tasks.stream().map(TaskResponse::new).collect(Collectors.toList());
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

        public TaskResponse(Task task) {
            this.id = task.getId();
            this.title = task.getTitle();
            this.description = task.getDescription();
            this.priority = task.getPriority();
            this.dueDateDays = task.getDueDateDays();
            this.status = task.getStatus();

            // ✅ SAFE ACCESS (no lazy crash)
            this.sectionName = task.getSection() != null
                    ? task.getSection().getSectionName()
                    : "General";

            this.assignees = task.getAssignees() != null
                    ? task.getAssignees()
                    : List.of();
        }
    }
}