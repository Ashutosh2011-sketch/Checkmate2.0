package com.checkmate.backend.controller;

import com.checkmate.backend.dto.*;
import com.checkmate.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")

//@PreAuthorize("hasAnyRole('ADMIN', 'ROLE_ADMIN')")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/department-stats")
    public ResponseEntity<List<DepartmentStatsDto>> getDepartmentStats() {
        return ResponseEntity.ok(reportService.getDepartmentStatistics());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<OverdueItemDto>> getOverdueItems(
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(reportService.getOverdueItems(department));
    }

    @GetMapping("/user-performance")
    public ResponseEntity<List<UserPerformanceDto>> getUserPerformance(
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(reportService.getUserPerformance(department));
    }

    @GetMapping("/completion-trends")
    public ResponseEntity<List<CompletionTrendDto>> getCompletionTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "MONTH") String groupBy) {
        return ResponseEntity.ok(reportService.getCompletionTrends(startDate, endDate, groupBy));
    }

    @GetMapping("/bottlenecks")
    public ResponseEntity<List<BottleneckDto>> getBottlenecks() {
        return ResponseEntity.ok(reportService.getBottleneckAnalysis());
    }
}