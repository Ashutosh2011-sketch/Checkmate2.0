// package com.checkmate.backend.service;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.checkmate.backend.dto.ChecklistDto;
// import com.checkmate.backend.dto.ChecklistSummaryDto;
// import com.checkmate.backend.dto.SectionDto;
// import com.checkmate.backend.dto.TaskDto;
// import com.checkmate.backend.entity.Checklist;
// import com.checkmate.backend.entity.Section;
// import com.checkmate.backend.entity.Task;
// import com.checkmate.backend.repository.AppUserRepository;
// import com.checkmate.backend.repository.ChecklistRepository;

// @Service
// public class ChecklistService {

//     private final ChecklistRepository repository;
//     private final NotificationService notificationService; // ✅ Naya
//     private final AppUserRepository userRepository; // ✅ Naya

//     public ChecklistService(ChecklistRepository repository, 
//                             NotificationService notificationService, 
//                             AppUserRepository userRepository) {
//         this.repository = repository;
//         this.notificationService = notificationService;
//         this.userRepository = userRepository;
//     }

//     @Transactional
//     public ChecklistDto save(ChecklistDto dto) {
//         // 1. Checklist save karo
//         Checklist entity = toEntity(dto);
//         Checklist saved = repository.save(entity);

//         // 2. 🔔 Notifications Trigger Karo
//         triggerAssignmentNotifications(dto);

//         return toDto(saved);
//     }

//     private void triggerAssignmentNotifications(ChecklistDto dto) {
//         // Unique users nikalne ke liye Set use karenge taaki ek user ko 10 bar notif na jaye
//         java.util.Set<String> uniqueAssigneeNames = new java.util.HashSet<>();

//         if (dto.getSections() != null) {
//             for (SectionDto section : dto.getSections()) {
//                 if (section.getTasks() != null) {
//                     for (TaskDto task : section.getTasks()) {
//                         if (task.getAssignees() != null) {
//                             uniqueAssigneeNames.addAll(task.getAssignees());
//                         }
//                     }
//                 }
//             }
//         }

//         // Har unique user ke liye notification create karo
//         for (String fullName : uniqueAssigneeNames) {
//             try {
//                 // String "Pratik (IT)" se "Pratik" nikalna
//                 String nameOnly = fullName.split("\\(")[0].trim();

//                 userRepository.findByName(nameOnly).ifPresent(user -> {
//                     notificationService.createNotification(
//                         user, 
//                         "Admin has assigned you a new checklist: " + dto.getChecklistName(), 
//                         "INFO"
//                     );
//                 });
//             } catch (Exception e) {
//                 System.err.println("Could not send notification to: " + fullName);
//             }
//         }
//     }

// @Transactional(readOnly = true)
//     public List<ChecklistDto> getAll() {
//         return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
//     }

//     @Transactional(readOnly = true)
//     public List<ChecklistSummaryDto> getAllSummaries() {
//         return repository.findAllSummaries();
//     }

//     @Transactional
//     public ChecklistDto save(ChecklistDto dto) {
//         Checklist entity = toEntity(dto);
//         Checklist saved = repository.save(entity);
//         return toDto(saved);
//     }

//     private Checklist toEntity(ChecklistDto dto) {
//         Checklist checklist = new Checklist();
//         checklist.setChecklistName(dto.getChecklistName());
//         checklist.setDepartment(dto.getDepartment());
//         checklist.setVisibility(dto.getVisibility());
//         checklist.setWorkflowType(dto.getWorkflowType());
//         checklist.setCompleted(dto.isCompleted());

//         List<Section> sections = new ArrayList<>();
//         if (dto.getSections() != null) {
//             for (SectionDto sectionDto : dto.getSections()) {
//                 Section section = new Section();
//                 section.setSectionName(sectionDto.getSectionName());

//                 List<Task> tasks = new ArrayList<>();
//                 if (sectionDto.getTasks() != null) {
//                     for (TaskDto taskDto : sectionDto.getTasks()) {
//                         Task task = new Task();
//                         task.setTitle(taskDto.getTitle());
//                         task.setDescription(taskDto.getDescription());
//                         task.setAssignees(taskDto.getAssignees());
//                         task.setPriority(taskDto.getPriority());
//                         task.setDueDateDays(taskDto.getDueDateDays());
//                         task.setDependsOn(taskDto.getDependsOn());
//                         task.setConditionDependentOn(taskDto.getConditionDependentOn());
//                         task.setConditionExpectedOutcome(taskDto.getConditionExpectedOutcome());
//                         task.setRemindBefore(taskDto.getRemindBefore());
//                         task.setEscalateTo(taskDto.getEscalateTo());
//                         task.setShowAdvanced(taskDto.isShowAdvanced());
//                         tasks.add(task);
//                     }
//                 }
//                 section.setTasks(tasks);
//                 sections.add(section);
//             }
//         }
//         checklist.setSections(sections);

//         return checklist;
//     }

//     private ChecklistDto toDto(Checklist checklist) {
//         ChecklistDto dto = new ChecklistDto();
//         dto.setId(checklist.getId());
//         dto.setChecklistName(checklist.getChecklistName());
//         dto.setDepartment(checklist.getDepartment());
//         dto.setVisibility(checklist.getVisibility());
//         dto.setWorkflowType(checklist.getWorkflowType());
//         dto.setCompleted(checklist.isCompleted());

//         List<SectionDto> sectionDtos = new ArrayList<>();
//         if (checklist.getSections() != null) {
//             for (Section section : checklist.getSections()) {
//                 SectionDto sectionDto = new SectionDto();
//                 sectionDto.setId(section.getId());
//                 sectionDto.setSectionName(section.getSectionName());

//                 List<TaskDto> taskDtos = new ArrayList<>();
//                 if (section.getTasks() != null) {
//                     for (Task task : section.getTasks()) {
//                         TaskDto taskDto = new TaskDto();
//                         taskDto.setId(task.getId());
//                         taskDto.setTitle(task.getTitle());
//                         taskDto.setDescription(task.getDescription());
//                         taskDto.setAssignees(task.getAssignees());
//                         taskDto.setPriority(task.getPriority());
//                         taskDto.setDueDateDays(task.getDueDateDays());
//                         taskDto.setDependsOn(task.getDependsOn());
//                         taskDto.setConditionDependentOn(task.getConditionDependentOn());
//                         taskDto.setConditionExpectedOutcome(task.getConditionExpectedOutcome());
//                         taskDto.setRemindBefore(task.getRemindBefore());
//                         taskDto.setEscalateTo(task.getEscalateTo());
//                         taskDto.setShowAdvanced(task.isShowAdvanced());
//                         taskDtos.add(taskDto);
//                     }
//                 }
//                 sectionDto.setTasks(taskDtos);
//                 sectionDtos.add(sectionDto);
//             }
//         }
//         dto.setSections(sectionDtos);
//         return dto;
//     }
// }

package com.checkmate.backend.service;

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
    private final NotificationService notificationService; // ✅ Injecting NotificationService
    private final AppUserRepository userRepository; // ✅ Injecting UserRepository

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
    public ChecklistDto save(ChecklistDto dto) {
        // 1. Checklist aur uske tasks ko entity mein convert karke save karo
        Checklist entity = toEntity(dto);
        Checklist saved = repository.save(entity);

        // 2. 🔔 Notifications Trigger Karo (Ye background mein users ko notify karega)
        triggerAssignmentNotifications(dto);

        return toDto(saved);
    }

    /**
     * Checklist se unique assignees nikal kar unhe Notify karne wala helper method
     */
    private void triggerAssignmentNotifications(ChecklistDto dto) {
        Set<String> uniqueAssigneeFullNames = new HashSet<>();

        // Loop: Sabhi sections aur tasks se unique names ikkatta karo
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

        // Har unique user ke liye DB search karo aur Notification create karo
        for (String fullName : uniqueAssigneeFullNames) {
            try {
                // Format "Pratik (IT)" se "Pratik" nikalne ke liye logic
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
                // Agar kisi ek user ka notif fail ho, toh baki checklist save hone mein rukawat
                // na aaye
                System.err.println("DEBUG: Failed to notify " + fullName + ". Reason: " + e.getMessage());
            }
        }
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