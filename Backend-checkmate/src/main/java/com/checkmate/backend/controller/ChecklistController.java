package com.checkmate.backend.controller;

import com.checkmate.backend.dto.ChecklistDto;
import com.checkmate.backend.dto.ChecklistSummaryDto;
import com.checkmate.backend.service.ChecklistService;
import com.checkmate.backend.util.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/checklists")
@CrossOrigin(origins = "${app.cors.allowed-origin:http://localhost:4200}") 
public class ChecklistController {

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @PostMapping("/create")
    public ChecklistDto createChecklist(@RequestBody ChecklistDto checklistDto, Principal principal,
            HttpServletRequest request) {
        return checklistService.save(checklistDto, principal.getName(), ClientIpResolver.resolve(request));
    }

    @GetMapping("/all")
    public List<ChecklistSummaryDto> getAllChecklists(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String role) {
        return checklistService.getVisibleSummaries(userName, role);
    }
}
