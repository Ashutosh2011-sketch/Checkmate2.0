package com.checkmate.backend.service;

import com.checkmate.backend.dto.DashboardDto;
import com.checkmate.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final TaskRepository taskRepository;

    public DashboardService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public DashboardDto getDashboard(String userName) {

        List<String> tasks = taskRepository.findTasksByUserName(userName);

        DashboardDto dto = new DashboardDto();

        // 🔥 MATCH FRONTEND EXACTLY
        dto.setAssignedChecklists(tasks);
        dto.setClaimedTasks(tasks);

        dto.setNotifications(List.of(
                "You have " + tasks.size() + " tasks assigned",
                "Complete your pending tasks"
        ));

        int progress = tasks.isEmpty() ? 0 : 50; // temp logic
        dto.setProgress(progress);

        return dto;
    }
}