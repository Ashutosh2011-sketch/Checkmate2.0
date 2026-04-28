package com.checkmate.backend.controller;

import com.checkmate.backend.dto.DashboardDto;
import com.checkmate.backend.dto.TaskInfoDto;
import com.checkmate.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
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
                                               @RequestBody Map<String, Object> body) {
        System.out.println("=== UPDATE TASK STATUS: taskId=" + taskId + " body=" + body + " ===");
        try {
            int percent = 0;
            Object val = body.get("completionPercent");
            if (val instanceof Number) {
                percent = ((Number) val).intValue();
            }
            TaskInfoDto result = service.updateTaskCompletion(taskId, percent);
            System.out.println("=== TASK STATUS UPDATED OK ===");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("=== TASK STATUS ERROR: " + e.getClass().getName() + " : " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @PutMapping("/tasks/{taskId}/complete")
    public ResponseEntity<?> markTaskComplete(@PathVariable Long taskId) {
        System.out.println("=== MARK TASK COMPLETE: taskId=" + taskId + " ===");
        try {
            TaskInfoDto result = service.markTaskComplete(taskId);
            System.out.println("=== TASK MARKED COMPLETE OK ===");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("=== MARK TASK ERROR: " + e.getClass().getName() + " : " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }

    @PutMapping("/checklists/{checklistId}/complete")
    public ResponseEntity<?> markChecklistComplete(@PathVariable Long checklistId) {
        System.out.println("=== MARK CHECKLIST COMPLETE: checklistId=" + checklistId + " ===");
        try {
            service.markChecklistComplete(checklistId);
            System.out.println("=== CHECKLIST MARKED COMPLETE OK ===");
            return ResponseEntity.ok(Map.of("message", "Checklist marked as completed"));
        } catch (Exception e) {
            System.out.println("=== MARK CHECKLIST ERROR: " + e.getClass().getName() + " : " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
        }
    }
}