package com.checkmate.backend.controller;

import com.checkmate.backend.dto.AccessLogEntryDto;
import com.checkmate.backend.service.ActivityLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security")
@CrossOrigin(origins = "http://localhost:4200")
public class SecurityComplianceController {

    private final ActivityLogService activityLogService;

    public SecurityComplianceController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping("/access-logs")
    public List<AccessLogEntryDto> getAccessLogs(@RequestParam(name = "limit", defaultValue = "200") int limit) {
        return activityLogService.fetchCombinedRecent(limit);
    }
}
