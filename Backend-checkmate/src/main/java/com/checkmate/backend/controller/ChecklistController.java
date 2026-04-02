package com.checkmate.backend.controller;

import com.checkmate.backend.dto.ChecklistDto;
import com.checkmate.backend.dto.ChecklistSummaryDto;
import com.checkmate.backend.service.ChecklistService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/checklists")
@CrossOrigin(origins = "http://localhost:4200") 
public class ChecklistController {

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @PostMapping("/create")
    public ChecklistDto createChecklist(@RequestBody ChecklistDto checklistDto) {
        return checklistService.save(checklistDto);
    }

    @GetMapping("/all")
    public List<ChecklistSummaryDto> getAllChecklists() {
        return checklistService.getAllSummaries();
    }
}
