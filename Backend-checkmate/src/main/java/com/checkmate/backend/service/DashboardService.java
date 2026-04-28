package com.checkmate.backend.service;

import com.checkmate.backend.dto.ChecklistInfoDto;
import com.checkmate.backend.dto.DashboardDto;
import com.checkmate.backend.dto.AdminDashboardSummaryDto;
import com.checkmate.backend.dto.TaskInfoDto;
import com.checkmate.backend.entity.Task;
import com.checkmate.backend.repository.ChecklistRepository;
import com.checkmate.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final TaskRepository taskRepository;
    private final ChecklistRepository checklistRepository;

    public DashboardService(TaskRepository taskRepository, ChecklistRepository checklistRepository) {
        this.taskRepository = taskRepository;
        this.checklistRepository = checklistRepository;
    }

    public DashboardDto getDashboard(String userName) {
        DashboardDto dto = new DashboardDto();

        try {
            System.out.println("DASH-DEBUG: Querying tasks for userName='" + userName + "'");
            List<Object[]> rawTasks = taskRepository.findFullTasksByUserName(userName);
            System.out.println("DASH-DEBUG: Found " + rawTasks.size() + " raw tasks");

            // Build task list and group by checklist — using RAW query data only (no entity loading)
            List<TaskInfoDto> claimedTasks = new ArrayList<>();
            Map<Long, String> checklistNames = new LinkedHashMap<>();
            Map<Long, List<TaskInfoDto>> checklistTaskMap = new LinkedHashMap<>();

            for (Object[] row : rawTasks) {
                try {
                    Long taskId = ((Number) row[0]).longValue();
                    String title = row[1] != null ? (String) row[1] : "Untitled";
                    String priority = row[2] != null ? (String) row[2] : "Medium";
                    String checklistName = row[5] != null ? (String) row[5] : "Unknown";
                    Long checklistId = ((Number) row[6]).longValue();

                    // Try to get completion data from the task entity
                    String status = "Pending";
                    int completionPercent = 0;
                    boolean completed = false;

                    try {
                        Optional<Task> optTask = taskRepository.findById(taskId);
                        if (optTask.isPresent()) {
                            Task entity = optTask.get();
                            status = entity.getStatus();
                            completionPercent = entity.getCompletionPercent();
                            completed = entity.isCompleted();
                        }
                    } catch (Exception e) {
                        // completion_percent column might not exist yet — use defaults
                        System.out.println("DASH-DEBUG: Could not load task entity " + taskId + ": " + e.getMessage());
                    }

                    TaskInfoDto taskDto = new TaskInfoDto(taskId, title, status, priority,
                            checklistName, completionPercent, completed);
                    claimedTasks.add(taskDto);

                    checklistNames.put(checklistId, checklistName);
                    checklistTaskMap.computeIfAbsent(checklistId, k -> new ArrayList<>()).add(taskDto);
                } catch (Exception e) {
                    System.out.println("DASH-DEBUG: Error processing task row: " + e.getMessage());
                }
            }

            // Build checklist lists: in-progress vs completed
            List<ChecklistInfoDto> inProgressChecklists = new ArrayList<>();
            List<ChecklistInfoDto> completedChecklists = new ArrayList<>();

            for (Map.Entry<Long, String> entry : checklistNames.entrySet()) {
                Long checklistId = entry.getKey();
                String name = entry.getValue();
                List<TaskInfoDto> tasks = checklistTaskMap.getOrDefault(checklistId, List.of());

                int totalTasks = tasks.size();
                int completedCount = 0;
                int totalPercent = 0;

                for (TaskInfoDto t : tasks) {
                    if (t.isCompleted()) {
                        completedCount++;
                        totalPercent += 100;
                    } else {
                        totalPercent += t.getCompletionPercent();
                    }
                }

                int avgProgress = totalTasks > 0 ? totalPercent / totalTasks : 0;

                ChecklistInfoDto clDto = new ChecklistInfoDto(checklistId, name, totalTasks, completedCount);
                clDto.setProgress(avgProgress);

                if (completedCount == totalTasks && totalTasks > 0) {
                    clDto.setStatus("Completed");
                    completedChecklists.add(clDto);
                } else {
                    clDto.setStatus("In Progress");
                    inProgressChecklists.add(clDto);
                }
            }

            // Compute overall progress
            int totalPercent = 0;
            for (TaskInfoDto t : claimedTasks) {
                totalPercent += t.isCompleted() ? 100 : t.getCompletionPercent();
            }
            int overallProgress = claimedTasks.size() > 0 ? totalPercent / claimedTasks.size() : 0;

            // Build notifications
            List<String> notifications = new ArrayList<>();
            if (claimedTasks.isEmpty()) {
                notifications.add("No tasks assigned to you yet.");
            } else {
                long pending = claimedTasks.stream().filter(t -> !t.isCompleted()).count();
                long done = claimedTasks.stream().filter(TaskInfoDto::isCompleted).count();
                if (pending > 0) {
                    notifications.add("You have " + pending + " pending task" + (pending > 1 ? "s" : "") + ".");
                }
                if (done > 0) {
                    notifications.add("Completed " + done + " task" + (done > 1 ? "s" : "") + "!");
                }
                if (!completedChecklists.isEmpty()) {
                    notifications.add(completedChecklists.size() + " checklist" + (completedChecklists.size() > 1 ? "s" : "") + " done.");
                }
            }

            dto.setAssignedChecklists(inProgressChecklists);
            dto.setCompletedChecklists(completedChecklists);
            dto.setClaimedTasks(claimedTasks);
            dto.setNotifications(notifications);
            dto.setProgress(overallProgress);

            System.out.println("DASH-DEBUG: Returning " + inProgressChecklists.size() + " in-progress, " 
                + completedChecklists.size() + " completed, " + claimedTasks.size() + " tasks");

        } catch (Exception e) {
            System.out.println("Dashboard error for user '" + userName + "': " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            dto.setAssignedChecklists(new ArrayList<>());
            dto.setCompletedChecklists(new ArrayList<>());
            dto.setClaimedTasks(new ArrayList<>());
            dto.setNotifications(List.of("No tasks assigned to you yet."));
            dto.setProgress(0);
        }

        return dto;
    }

    // Update task completion percentage
    public TaskInfoDto updateTaskCompletion(Long taskId, int percent) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        percent = Math.max(0, Math.min(percent, 100));
        task.setCompletionPercent(percent);

        if (percent >= 100) {
            task.setCompleted(true);
            task.setStatus("Completed");
        } else if (percent > 0) {
            task.setCompleted(false);
            task.setStatus("In Progress");
        } else {
            task.setCompleted(false);
            task.setStatus("Pending");
        }

        taskRepository.save(task);

        return new TaskInfoDto(task.getId(), task.getTitle(), task.getStatus(),
                task.getPriority(), null, task.getCompletionPercent(), task.isCompleted());
    }

    // Mark task as complete (100%)
    public TaskInfoDto markTaskComplete(Long taskId) {
        return updateTaskCompletion(taskId, 100);
    }

    // Mark all tasks in a checklist as complete
    public void markChecklistComplete(Long checklistId) {
        List<Task> tasks = taskRepository.findBySection_Checklist_Id(checklistId);
        for (Task task : tasks) {
            task.setCompletionPercent(100);
            task.setCompleted(true);
            task.setStatus("Completed");
        }
        taskRepository.saveAll(tasks);

        checklistRepository.findById(checklistId).ifPresent(checklist -> {
            checklist.setCompleted(true);
            checklistRepository.save(checklist);
        });
    }

    public AdminDashboardSummaryDto getAdminSummary() {
        long totalChecklists = checklistRepository.count();
        long totalTasks = taskRepository.count();
        long completedTasks = 0;
        try {
            completedTasks = taskRepository.countCompletedTasks();
        } catch (Exception e) {
            System.out.println("countCompletedTasks error: " + e.getMessage());
        }
        long pendingTasks = Math.max(totalTasks - completedTasks, 0);
        long completedChecklists = checklistRepository.countByCompletedTrue();

        AdminDashboardSummaryDto summary = new AdminDashboardSummaryDto();
        summary.setTotalChecklists(totalChecklists);
        summary.setTotalTasks(totalTasks);
        summary.setCompletedTasks(completedTasks);
        summary.setPendingTasks(pendingTasks);
        summary.setCompletedChecklists(completedChecklists);
        return summary;
    }
}