package com.checkmate.backend.service;

import com.checkmate.backend.dto.ChecklistInfoDto;
import com.checkmate.backend.dto.DashboardDto;
import com.checkmate.backend.dto.AdminDashboardSummaryDto;
import com.checkmate.backend.dto.TaskInfoDto;
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

        // 1. Get full task data for the user
        List<Object[]> rawTasks = taskRepository.findFullTasksByUserName(userName);

        // 2. Build TaskInfoDto list
        List<TaskInfoDto> claimedTasks = new ArrayList<>();
        Map<Long, String> checklistNames = new LinkedHashMap<>();
        Map<Long, Integer> checklistUserTaskCount = new LinkedHashMap<>();

        for (Object[] row : rawTasks) {
            Long taskId = ((Number) row[0]).longValue();
            String title = (String) row[1];
            String priority = (String) row[2];
            String checklistName = (String) row[5];
            Long checklistId = ((Number) row[6]).longValue();

            TaskInfoDto taskDto = new TaskInfoDto(taskId, title, "In Progress", priority, checklistName);
            claimedTasks.add(taskDto);

            checklistNames.put(checklistId, checklistName);
            checklistUserTaskCount.merge(checklistId, 1, Integer::sum);
        }

        // 3. Build ChecklistInfoDto list with real progress
        List<ChecklistInfoDto> assignedChecklists = new ArrayList<>();
        for (Map.Entry<Long, String> entry : checklistNames.entrySet()) {
            Long checklistId = entry.getKey();
            String name = entry.getValue();
            int totalTasks = taskRepository.countTasksByChecklistId(checklistId);
            int userTasks = checklistUserTaskCount.getOrDefault(checklistId, 0);

            // Progress = user's assigned tasks / total tasks in checklist * 100
            // This gives a sense of how much of the checklist has been covered
            int progress = totalTasks > 0 ? Math.min(userTasks * 100 / totalTasks, 100) : 0;

            assignedChecklists.add(new ChecklistInfoDto(name, totalTasks, 0));
            assignedChecklists.get(assignedChecklists.size() - 1).setProgress(progress);
        }

        // 4. Compute overall progress
        int totalAssigned = claimedTasks.size();
        int overallProgress = totalAssigned > 0 ? Math.min(totalAssigned * 10, 100) : 0;

        // 5. Build notifications
        List<String> notifications = new ArrayList<>();
        if (totalAssigned == 0) {
            notifications.add("No tasks assigned to you yet.");
        } else {
            notifications.add("You have " + totalAssigned + " task" + (totalAssigned > 1 ? "s" : "") + " assigned.");
            if (assignedChecklists.size() > 0) {
                notifications.add("Working on " + assignedChecklists.size() + " checklist" + (assignedChecklists.size() > 1 ? "s" : "") + ".");
            }
            notifications.add("Complete your pending tasks to improve progress.");
        }

        // 6. Build DTO
        DashboardDto dto = new DashboardDto();
        dto.setAssignedChecklists(assignedChecklists);
        dto.setClaimedTasks(claimedTasks);
        dto.setNotifications(notifications);
        dto.setProgress(overallProgress);

        return dto;
    }

    public AdminDashboardSummaryDto getAdminSummary() {
        long totalChecklists = checklistRepository.count();
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countCompletedTasks();
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