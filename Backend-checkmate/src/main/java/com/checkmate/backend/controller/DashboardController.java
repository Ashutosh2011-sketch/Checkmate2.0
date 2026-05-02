
package com.checkmate.backend.controller;

import com.checkmate.backend.dto.DashboardDto;
import com.checkmate.backend.dto.TaskInfoDto;
import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.repository.AppUserRepository;
import com.checkmate.backend.service.DashboardService;
import com.checkmate.backend.util.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService service;
    private final AppUserRepository userRepository;

    public DashboardController(DashboardService service, AppUserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userName}")
    public DashboardDto getDashboard(@PathVariable String userName) {
        System.out.println("=== DASHBOARD HIT for: " + userName + " ===");
        try {
            DashboardDto dto = service.getDashboard(userName);
            System.out.println("=== DASHBOARD SUCCESS ===");
            return dto;
        } catch (Exception e) {
            System.out.println("=== DASHBOARD EXCEPTION: " + e.getClass().getName() + " : " + e.getMessage() + " ===");
            e.printStackTrace();
            DashboardDto dto = new DashboardDto();
            dto.setAssignedChecklists(new ArrayList<>());
            dto.setCompletedChecklists(new ArrayList<>());
            dto.setClaimedTasks(new ArrayList<>());
            dto.setNotifications(new ArrayList<>());
            dto.setProgress(0);
            return dto;
        }
    }

    @GetMapping("/admin/summary")
    public ResponseEntity<?> getAdminSummary() {
        try {
            return ResponseEntity.ok(service.getAdminSummary());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId,
            @RequestBody Map<String, Object> body, Principal principal, HttpServletRequest httpRequest) {
        System.out.println("=== UPDATE TASK STATUS: taskId=" + taskId + " body=" + body + " ===");
        try {
            int percent = 0;
            Object val = body.get("completionPercent");
            if (val instanceof Number) {
                percent = ((Number) val).intValue();
            }
            String completedBy = principal != null ? principal.getName() : null;
            TaskInfoDto result = service.updateTaskCompletion(taskId, percent, completedBy,
                    ClientIpResolver.resolve(httpRequest));
            System.out.println("=== TASK STATUS UPDATED OK ===");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @PutMapping("/tasks/{taskId}/complete")
    public ResponseEntity<?> markTaskComplete(@PathVariable Long taskId, Principal principal) {
        System.out.println("=== MARK TASK COMPLETE: taskId=" + taskId + " ===");
        try {
            TaskInfoDto result = service.markTaskComplete(taskId, principal.getName());
            System.out.println("=== TASK MARKED COMPLETE OK ===");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @PutMapping("/checklists/{checklistId}/complete")
    public ResponseEntity<?> markChecklistComplete(@PathVariable Long checklistId, Principal principal) {
        System.out.println("=== MARK CHECKLIST COMPLETE: checklistId=" + checklistId + " ===");
        try {
            String email = principal.getName();
            String userName = getUserName(email);
            service.markChecklistComplete(checklistId, userName, email);
            System.out.println("=== CHECKLIST MARKED COMPLETE OK ===");
            return ResponseEntity.ok(Map.of("message", "Checklist marked as completed"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    private String getUserName(String email) {
        return userRepository.findByEmail(email)
                .map(AppUser::getName)
                .orElse("A user");
    }
}