package com.checkmate.backend.scheduler;

import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.entity.Task;
import com.checkmate.backend.repository.AppUserRepository;
import com.checkmate.backend.repository.TaskRepository;
import com.checkmate.backend.service.EmailService;
import com.checkmate.backend.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {

    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public ReminderScheduler(TaskRepository taskRepository,
            AppUserRepository userRepository,
            NotificationService notificationService,
            EmailService emailService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    // Runs every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDueDateReminders() {
        System.out.println("REMINDER-SCHEDULER: Running due date check at " + LocalDateTime.now());

        List<Task> incompleteTasks = taskRepository.findByCompletedFalse();

        for (Task task : incompleteTasks) {

            int dueDays = task.getDueDateDays();
            if (dueDays != 1)
                continue;

            String checklistName = task.getSection().getChecklist().getChecklistName();

            for (String assigneeName : task.getAssignees()) {

                String nameOnly = assigneeName.contains("(")
                        ? assigneeName.split("\\(")[0].trim()
                        : assigneeName.trim();

                userRepository.findByName(nameOnly).ifPresent(user -> {
                    // In-app notification
                    notificationService.createNotification(
                            user,
                            "Reminder: Task \"" + task.getTitle()
                                    + "\" is due tomorrow! Checklist: " + checklistName,
                            "REMINDER");

                    // Email reminder
                    if (user.getPersonalEmail() != null
                            && !user.getPersonalEmail().isEmpty()) {
                        emailService.sendReminderEmail(
                                user.getPersonalEmail(),
                                user.getName(),
                                task.getTitle(),
                                checklistName);
                    }
                });
            }
        }

        System.out.println("REMINDER-SCHEDULER: Done sending reminders");
    }
}
