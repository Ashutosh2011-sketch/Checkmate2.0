package com.checkmate.backend.service;

import com.checkmate.backend.dto.ChecklistInfoDto;
import com.checkmate.backend.dto.DashboardDto;
import com.checkmate.backend.dto.AdminDashboardSummaryDto;
import com.checkmate.backend.dto.TaskInfoDto;
import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.entity.Task;
import com.checkmate.backend.repository.AppUserRepository;
import com.checkmate.backend.repository.ChecklistRepository;
import com.checkmate.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.repository.TaskCommentRepository;
import com.checkmate.backend.repository.TaskAttachmentRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DashboardService {

    private final TaskRepository taskRepository;
    private final ChecklistRepository checklistRepository;

    private final NotificationService notificationService;
    private final AppUserRepository userRepository;
    private final ActivityLogService activityLogService;

    private final TaskCommentRepository commentRepository;
    private final TaskAttachmentRepository attachmentRepository;

    public DashboardService(TaskRepository taskRepository, ChecklistRepository checklistRepository,
            NotificationService notificationService, AppUserRepository userRepository,
            ActivityLogService activityLogService, TaskCommentRepository commentRepository,
            TaskAttachmentRepository attachmentRepository) {
        this.taskRepository = taskRepository;
        this.checklistRepository = checklistRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
        this.commentRepository = commentRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboard(String userName) {
        DashboardDto dto = new DashboardDto();

        try {
            System.out.println("DASH-DEBUG: Querying tasks for userName='" + userName + "'");
            List<Object[]> rawTasks = taskRepository.findFullTasksByUserName(userName);
            System.out.println("DASH-DEBUG: Found " + rawTasks.size() + " raw tasks");

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

                    // Try to get completion data from task entity
                    String status = "Pending";
                    int completionPercent = 0;
                    boolean completed = false;
                    LocalDateTime completedAt = null;
                    String completedBy = null;

                    try {
                        Optional<Task> optTask = taskRepository.findById(taskId);
                        if (optTask.isPresent()) {
                            Task entity = optTask.get();
                            status = entity.getStatus();
                            completionPercent = entity.getCompletionPercent() != null
                                    ? entity.getCompletionPercent()
                                    : 0;
                            completed = entity.isCompleted();
                            completedAt = entity.getCompletedAt();
                            completedBy = entity.getCompletedBy();
                        }
                    } catch (Exception e) {
                        System.out.println("DASH-DEBUG: Could not load task " + taskId + ": " + e.getMessage());
                    }

                    TaskInfoDto taskDto = new TaskInfoDto(taskId, title, status, priority,
                            checklistName, completionPercent, completed);
                    taskDto.setCompletedAt(completedAt);
                    taskDto.setCompletedBy(completedBy);
                    claimedTasks.add(taskDto);

                    checklistNames.put(checklistId, checklistName);
                    checklistTaskMap.computeIfAbsent(checklistId, k -> new ArrayList<>()).add(taskDto);
                } catch (Exception e) {
                    System.out.println("DASH-DEBUG: Error processing row: " + e.getMessage());
                }
            }

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

            int totalPercent = 0;
            for (TaskInfoDto t : claimedTasks) {
                totalPercent += t.isCompleted() ? 100 : t.getCompletionPercent();
            }
            int overallProgress = claimedTasks.size() > 0 ? totalPercent / claimedTasks.size() : 0;

            List<String> notifications = new ArrayList<>();
            if (claimedTasks.isEmpty()) {
                notifications.add("No tasks assigned to you yet.");
            } else {
                long pending = claimedTasks.stream().filter(t -> !t.isCompleted()).count();
                long done = claimedTasks.stream().filter(TaskInfoDto::isCompleted).count();
                if (pending > 0)
                    notifications.add("You have " + pending + " pending task" + (pending > 1 ? "s" : "") + ".");
                if (done > 0)
                    notifications.add("Completed " + done + " task" + (done > 1 ? "s" : "") + "!");
                if (!completedChecklists.isEmpty())
                    notifications.add(completedChecklists.size() + " checklist(s) done.");
            }

            dto.setAssignedChecklists(inProgressChecklists);
            dto.setCompletedChecklists(completedChecklists);
            dto.setClaimedTasks(claimedTasks);
            dto.setNotifications(notifications);
            dto.setProgress(overallProgress);

        } catch (Exception e) {
            System.out.println(
                    "Dashboard error for user '" + userName + "': " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            dto.setAssignedChecklists(new ArrayList<>());
            dto.setCompletedChecklists(new ArrayList<>());
            dto.setClaimedTasks(new ArrayList<>());
            dto.setNotifications(List.of("No tasks assigned to you yet."));
            dto.setProgress(0);
        }

        return dto;
    }

    @Transactional
    public TaskInfoDto updateTaskCompletion(Long taskId, int percent, String completedByEmail, String clientIp) {
        System.out.println("SERVICE: updateTaskCompletion taskId=" + taskId + " percent=" + percent);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        percent = Math.max(0, Math.min(percent, 100));
        task.setCompletionPercent(percent);

        if (percent >= 100) {
            task.setCompleted(true);
            task.setStatus("Completed");
            task.setCompletedAt(LocalDateTime.now());
            task.setCompletedBy(
                    completedByEmail != null && !completedByEmail.isBlank() ? completedByEmail : null);
        } else {
            task.setCompleted(false);
            task.setCompletedAt(null);
            task.setCompletedBy(null);
            if (percent > 0) {
                task.setStatus("In Progress");
            } else {
                task.setStatus("Pending");
            }
        }

        task = taskRepository.save(task);
        System.out.println(
                "SERVICE: Task saved OK - completed=" + task.isCompleted() + " percent=" + task.getCompletionPercent());

        if (percent > 0 && percent < 100 && completedByEmail != null && !completedByEmail.isBlank()) {
            String checklistTitle = "—";
            try {
                if (task.getSection() != null && task.getSection().getChecklist() != null) {
                    checklistTitle = task.getSection().getChecklist().getChecklistName();
                }
            } catch (Exception ignored) {
                /* lazy load edge cases */
            }
            String summary = task.getTitle() + " — " + checklistTitle + " (" + percent + "%)";
            activityLogService.appendTaskUpdated(completedByEmail, summary, clientIp);
        }

        TaskInfoDto dto = new TaskInfoDto(task.getId(), task.getTitle(), task.getStatus(),
                task.getPriority(), null, task.getCompletionPercent(), task.isCompleted());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setCompletedBy(task.getCompletedBy());
        return dto;
    }

    @Transactional
    public TaskInfoDto markTaskComplete(Long taskId, String userEmail) {
        TaskInfoDto result = updateTaskCompletion(taskId, 100, userEmail, null);

        long commentCount = commentRepository.countByTaskId(taskId);
        long attachmentCount = attachmentRepository.countByTaskId(taskId);

        String userName = userRepository.findByEmail(userEmail)
                .map(AppUser::getName)
                .orElse("A user");

        Long checklistId = taskRepository.findById(taskId)
                .map(t -> t.getSection().getChecklist().getId())
                .orElse(null);

        StringBuilder message = new StringBuilder();
        message.append(userName)
                .append(" completed task: \"")
                .append(result.getTitle())
                .append("\"");

        if (commentCount > 0 && attachmentCount > 0) {
            message.append(" and left a comment and attached a file");
        } else if (commentCount > 0) {
            message.append(" and left a comment");
        } else if (attachmentCount > 0) {
            message.append(" and attached a file");
        }

        if (checklistId != null) {
            message.append(" | checklistId:").append(checklistId);
        }

        userRepository.findByRole("ADMIN").ifPresent(admin -> {
            notificationService.createNotification(
                    admin,
                    message.toString(),
                    "TASK_COMPLETE");
        });

        return result;
    }

    @Transactional
    public void markChecklistComplete(Long checklistId, String userName, String userEmail) {
        List<Task> tasks = taskRepository.findBySection_Checklist_Id(checklistId);

        long totalComments = 0;
        long totalAttachments = 0;
        for (Task task : tasks) {
            totalComments += commentRepository.countByTaskId(task.getId());
            totalAttachments += attachmentRepository.countByTaskId(task.getId());
        }

        final long finalComments = totalComments;
        final long finalAttachments = totalAttachments;

        for (Task task : tasks) {
            task.setCompletionPercent(100);
            task.setCompleted(true);
            task.setStatus("Completed");
        }
        taskRepository.saveAll(tasks);

        checklistRepository.findById(checklistId).ifPresent(checklist -> {
            checklist.setCompleted(true);
            checklist.setCompletedAt(LocalDateTime.now());
            checklistRepository.save(checklist);

            StringBuilder message = new StringBuilder();
            message.append(userName)
                    .append(" completed checklist: \"")
                    .append(checklist.getChecklistName())
                    .append("\"");

            if (finalComments > 0 && finalAttachments > 0) {
                message.append(" with comments and attachments");
            } else if (finalComments > 0) {
                message.append(" with comments");
            } else if (finalAttachments > 0) {
                message.append(" with attachments");
            }

            message.append(" | checklistId:").append(checklistId);

            userRepository.findByRole("ADMIN").ifPresent(admin -> {
                notificationService.createNotification(
                        admin,
                        message.toString(),
                        "CHECKLIST_COMPLETE");
            });
        });
    }

    @Transactional(readOnly = true)
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