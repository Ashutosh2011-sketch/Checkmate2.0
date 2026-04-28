package com.checkmate.backend.controller;

import com.checkmate.backend.dto.DashboardDto;
import com.checkmate.backend.dto.ChecklistInfoDto;
import com.checkmate.backend.dto.TaskInfoDto;
import com.checkmate.backend.dto.AdminDashboardSummaryDto;
import com.checkmate.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
            // Return empty dashboard instead of error
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId,
                                               @RequestBody Map<String, Integer> body) {
        try {
            int percent = body.getOrDefault("completionPercent", 0);
            TaskInfoDto result = service.updateTaskCompletion(taskId, percent);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/tasks/{taskId}/complete")
    public ResponseEntity<?> markTaskComplete(@PathVariable Long taskId) {
        try {
            TaskInfoDto result = service.markTaskComplete(taskId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/checklists/{checklistId}/complete")
    public ResponseEntity<?> markChecklistComplete(@PathVariable Long checklistId) {
        try {
            service.markChecklistComplete(checklistId);
            return ResponseEntity.ok(Map.of("message", "Checklist marked as completed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}