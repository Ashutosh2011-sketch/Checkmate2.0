package com.checkmate.backend.controller;

import com.checkmate.backend.dto.TemplateDto;
import com.checkmate.backend.dto.TemplateVersionDto;
import com.checkmate.backend.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "http://localhost:4200")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    // GET all templates
    @GetMapping
    public ResponseEntity<List<TemplateDto>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    // GET template by id
    @GetMapping("/{id}")
    public ResponseEntity<TemplateDto> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    // POST create template
    @PostMapping
    public ResponseEntity<TemplateDto> createTemplate(
            @RequestBody TemplateDto dto,
            @RequestHeader(value = "X-User-Name", defaultValue = "Admin") String createdBy) {
        return ResponseEntity.ok(templateService.createTemplate(dto, createdBy));
    }

    // PUT update template
    @PutMapping("/{id}")
    public ResponseEntity<TemplateDto> updateTemplate(
            @PathVariable Long id,
            @RequestBody TemplateDto dto,
            @RequestParam(required = false) String changeNote,
            @RequestHeader(value = "X-User-Name", defaultValue = "Admin") String updatedBy) {
        return ResponseEntity.ok(templateService.updateTemplate(id, dto, updatedBy, changeNote));
    }

    // DELETE template
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(Map.of("message", "Template deleted successfully"));
    }

    // GET all versions of a template
    @GetMapping("/{id}/versions")
    public ResponseEntity<List<TemplateVersionDto>> getVersions(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getVersions(id));
    }

    // POST restore a version
    @PostMapping("/{templateId}/versions/{versionId}/restore")
    public ResponseEntity<TemplateDto> restoreVersion(
            @PathVariable Long templateId,
            @PathVariable Long versionId,
            @RequestHeader(value = "X-User-Name", defaultValue = "Admin") String restoredBy) {
        return ResponseEntity.ok(templateService.restoreVersion(templateId, versionId, restoredBy));
    }
}