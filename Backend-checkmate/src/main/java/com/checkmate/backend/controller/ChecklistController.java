package com.checkmate.backend.controller;

import com.checkmate.backend.entity.Checklist;
import com.checkmate.backend.repository.ChecklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
@CrossOrigin(origins = "http://localhost:4200") 
public class ChecklistController {

    @Autowired
    private ChecklistRepository checklistRepository;

    @PostMapping("/create")
    public Checklist createChecklist(@RequestBody Checklist checklist) {
        System.out.println("Angular sent a new checklist: " + checklist.getChecklistName());
        return checklistRepository.save(checklist); 
    }

    @GetMapping("/all")
    public List<Checklist> getAllChecklists() {
        return checklistRepository.findAll();
    }
}