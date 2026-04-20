package com.checkmate.backend.controller;

import com.checkmate.backend.dto.DashboardDto;
import com.checkmate.backend.dto.AdminDashboardSummaryDto;
import com.checkmate.backend.service.DashboardService;
import org.springframework.web.bind.annotation.*;

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
        return service.getDashboard(userName);
    }

    @GetMapping("/admin/summary")
    public AdminDashboardSummaryDto getAdminSummary() {
        return service.getAdminSummary();
    }
}