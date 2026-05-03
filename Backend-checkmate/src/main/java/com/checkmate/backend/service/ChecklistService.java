
package com.checkmate.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.dto.ChecklistDto;
import com.checkmate.backend.dto.ChecklistSummaryDto;
import com.checkmate.backend.dto.SectionDto;
import com.checkmate.backend.dto.TaskDto;
import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.entity.Checklist;
import com.checkmate.backend.entity.Section;
import com.checkmate.backend.entity.Task;
import com.checkmate.backend.repository.AppUserRepository;
import com.checkmate.backend.repository.ChecklistRepository;

@Service
public class ChecklistService {

    private final ChecklistRepository repository;
    private final NotificationService notificationService;
    private final AppUserRepository userRepository;

    public ChecklistService(ChecklistRepository repository,
            NotificationService notificationService,
            AppUserRepository userRepository) {
        this.repository = repository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ChecklistDto> getAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChecklistSummaryDto> getAllSummaries() {
        return repository.findAllSummaries();
    }

    @Transactional
    public ChecklistDto save(ChecklistDto dto, String creatorEmail, String clientIp) {

        Checklist entity = toEntity(dto, creatorEmail, clientIp);
        Checklist saved = repository.save(entity);

        triggerAssignmentNotifications(dto);

        return toDto(saved);
    }

    private void triggerAssignmentNotifications(ChecklistDto dto) {
        Set<String> uniqueAssigneeFullNames = new HashSet<>();

        if (dto.getSections() != null) {
            for (SectionDto section : dto.getSections()) {
                if (section.getTasks() != null) {
                    for (TaskDto task : section.getTasks()) {
                        if (task.getAssignees() != null) {
                            uniqueAssigneeFullNames.addAll(task.getAssignees());
                        }
                    }
                }
            }
        }

        for (String fullName : uniqueAssigneeFullNames) {
            try {

                String nameOnly = fullName.contains("(")
                        ? fullName.split("\\(")[0].trim()
                        : fullName.trim();

                userRepository.findByName(nameOnly).ifPresent(user -> {
                    notificationService.createNotification(
                            user,
                            "Admin has assigned you a new checklist: " + dto.getChecklistName(),
                            "INFO");
                    System.out.println("DEBUG: Notification successfully sent to " + nameOnly);
                });
            } catch (Exception e) {

                System.err.println("DEBUG: Failed to notify " + fullName + ". Reason: " + e.getMessage());
            }
        }
    }

    private Checklist toEntity(ChecklistDto dto, String creatorEmail, String clientIp) {
        Checklist checklist = new Checklist();
        checklist.setChecklistName(dto.getChecklistName());
        checklist.setDepartment(dto.getDepartment());
        checklist.setVisibility(dto.getVisibility());
        checklist.setWorkflowType(dto.getWorkflowType());
        checklist.setCompleted(dto.isCompleted());
        checklist.setCreatedAt(LocalDateTime.now());
        checklist.setCreatedBy(creatorEmail);
        checklist.setCreatedIp(clientIp);

        List<Section> sections = new ArrayList<>();

        if (dto.getSections() != null) {
            for (SectionDto sectionDto : dto.getSections()) {

                Section section = new Section();
                section.setSectionName(sectionDto.getSectionName());

                List<Task> tasks = new ArrayList<>();

                if (sectionDto.getTasks() != null) {
                    for (TaskDto taskDto : sectionDto.getTasks()) {

                        Task task = new Task();
                        task.setTitle(taskDto.getTitle());
                        task.setDescription(taskDto.getDescription());
                        task.setAssignees(taskDto.getAssignees());
                        task.setPriority(taskDto.getPriority());
                        task.setDueDateDays(taskDto.getDueDateDays());

                        task.setStatus(taskDto.getStatus() != null ? taskDto.getStatus() : "Pending");
                        task.setDependsOn(taskDto.getDependsOn());
                        task.setConditionDependentOn(taskDto.getConditionDependentOn());
                        task.setConditionExpectedOutcome(taskDto.getConditionExpectedOutcome());
                        task.setRemindBefore(taskDto.getRemindBefore());
                        task.setEscalateTo(taskDto.getEscalateTo());
                        task.setShowAdvanced(taskDto.isShowAdvanced());

                        tasks.add(task);
                    }
                }

                section.setTasks(tasks);
                sections.add(section);
            }
        }
        checklist.setSections(sections);
        return checklist;
    }

    private ChecklistDto toDto(Checklist checklist) {
        ChecklistDto dto = new ChecklistDto();

        dto.setId(checklist.getId());
        dto.setChecklistName(checklist.getChecklistName());
        dto.setDepartment(checklist.getDepartment());
        dto.setVisibility(checklist.getVisibility());
        dto.setWorkflowType(checklist.getWorkflowType());
        dto.setCompleted(checklist.isCompleted());
        dto.setCreatedAt(checklist.getCreatedAt());
        dto.setCreatedBy(checklist.getCreatedBy());
        dto.setCreatedIp(checklist.getCreatedIp());

        List<SectionDto> sectionDtos = new ArrayList<>();

        if (checklist.getSections() != null) {
            for (Section section : checklist.getSections()) {

                SectionDto sectionDto = new SectionDto();
                sectionDto.setId(section.getId());
                sectionDto.setSectionName(section.getSectionName());

                List<TaskDto> taskDtos = new ArrayList<>();

                if (section.getTasks() != null) {
                    for (Task task : section.getTasks()) {

                        TaskDto taskDto = new TaskDto();
                        taskDto.setId(task.getId());
                        taskDto.setTitle(task.getTitle());
                        taskDto.setDescription(task.getDescription());
                        taskDto.setAssignees(task.getAssignees());
                        taskDto.setPriority(task.getPriority());
                        taskDto.setDueDateDays(task.getDueDateDays());
                        taskDto.setStatus(task.getStatus());
                        taskDto.setDependsOn(task.getDependsOn());
                        taskDto.setConditionDependentOn(task.getConditionDependentOn());
                        taskDto.setConditionExpectedOutcome(task.getConditionExpectedOutcome());
                        taskDto.setRemindBefore(task.getRemindBefore());
                        taskDto.setEscalateTo(task.getEscalateTo());
                        taskDto.setShowAdvanced(task.isShowAdvanced());
                        taskDto.setCompletedAt(task.getCompletedAt());
                        taskDto.setCompletedBy(task.getCompletedBy());

                        taskDtos.add(taskDto);
                    }
                }

                sectionDto.setTasks(taskDtos);
                sectionDtos.add(sectionDto);
            }
        }

        dto.setSections(sectionDtos);
        return dto;
    }
}