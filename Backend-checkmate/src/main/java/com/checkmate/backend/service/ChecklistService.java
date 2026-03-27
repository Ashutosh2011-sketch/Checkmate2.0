package com.checkmate.backend.service;

import com.checkmate.backend.dto.ChecklistDto;
import com.checkmate.backend.dto.SectionDto;
import com.checkmate.backend.dto.TaskDto;
import com.checkmate.backend.entity.Checklist;
import com.checkmate.backend.entity.Section;
import com.checkmate.backend.entity.Task;
import com.checkmate.backend.repository.ChecklistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChecklistService {

    private final ChecklistRepository repository;

    public ChecklistService(ChecklistRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ChecklistDto> getAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ChecklistDto save(ChecklistDto dto) {
        Checklist entity = toEntity(dto);
        Checklist saved = repository.save(entity);
        return toDto(saved);
    }

    private Checklist toEntity(ChecklistDto dto) {
        Checklist checklist = new Checklist();
        checklist.setChecklistName(dto.getChecklistName());
        checklist.setDepartment(dto.getDepartment());
        checklist.setVisibility(dto.getVisibility());
        checklist.setWorkflowType(dto.getWorkflowType());
        checklist.setCompleted(dto.isCompleted());

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
                        taskDto.setDependsOn(task.getDependsOn());
                        taskDto.setConditionDependentOn(task.getConditionDependentOn());
                        taskDto.setConditionExpectedOutcome(task.getConditionExpectedOutcome());
                        taskDto.setRemindBefore(task.getRemindBefore());
                        taskDto.setEscalateTo(task.getEscalateTo());
                        taskDto.setShowAdvanced(task.isShowAdvanced());
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