package com.checkmate.backend.service;

import com.checkmate.backend.dto.TemplateDto;
import com.checkmate.backend.dto.TemplateVersionDto;
import com.checkmate.backend.entity.ChecklistTemplate;
import com.checkmate.backend.entity.TemplateVersion;
import com.checkmate.backend.repository.TemplateRepository;
import com.checkmate.backend.repository.TemplateVersionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateVersionRepository versionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ── GET ALL ──────────────────────────────────────────────────────────────
    public List<TemplateDto> getAllTemplates() {
        return templateRepository.findAllByOrderByUpdatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── GET BY ID ────────────────────────────────────────────────────────────
    public TemplateDto getTemplateById(Long id) {
        ChecklistTemplate t = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));
        return toDto(t);
    }

    // ── CREATE ───────────────────────────────────────────────────────────────
    public TemplateDto createTemplate(TemplateDto dto, String createdBy) {
        ChecklistTemplate t = new ChecklistTemplate();
        t.setTemplateName(dto.getTemplateName());
        t.setDepartment(dto.getDepartment());
        t.setVisibility(dto.getVisibility());
        t.setWorkflowType(dto.getWorkflowType());
        t.setDescription(dto.getDescription());
        t.setCreatedBy(createdBy);
        t.setCurrentVersion(1);
        ChecklistTemplate saved = templateRepository.save(t);

        // Save first version snapshot
        saveVersionSnapshot(saved, 1, dto, "Initial version", createdBy);

        return toDto(saved);
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    public TemplateDto updateTemplate(Long id, TemplateDto dto, String updatedBy, String changeNote) {
        ChecklistTemplate t = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));

        t.setTemplateName(dto.getTemplateName());
        t.setDepartment(dto.getDepartment());
        t.setVisibility(dto.getVisibility());
        t.setWorkflowType(dto.getWorkflowType());
        t.setDescription(dto.getDescription());

        int newVersion = t.getCurrentVersion() + 1;
        t.setCurrentVersion(newVersion);

        ChecklistTemplate saved = templateRepository.save(t);

        // Save new version snapshot
        saveVersionSnapshot(saved, newVersion, dto,
                changeNote != null ? changeNote : "Version " + newVersion, updatedBy);

        return toDto(saved);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    // ── GET VERSIONS ─────────────────────────────────────────────────────────
    public List<TemplateVersionDto> getVersions(Long templateId) {
        return versionRepository.findByTemplateIdOrderByVersionDesc(templateId)
                .stream().map(v -> {
                    TemplateVersionDto d = new TemplateVersionDto();
                    d.setId(v.getId());
                    d.setVersion(v.getVersion());
                    d.setSnapshot(v.getSnapshot());
                    d.setChangeNote(v.getChangeNote());
                    d.setCreatedBy(v.getCreatedBy());
                    d.setCreatedAt(v.getCreatedAt());
                    return d;
                }).collect(Collectors.toList());
    }

    // ── RESTORE VERSION ──────────────────────────────────────────────────────
    public TemplateDto restoreVersion(Long templateId, Long versionId, String restoredBy) {
        TemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));

        try {
            TemplateDto snapshot = objectMapper.readValue(version.getSnapshot(), TemplateDto.class);
            return updateTemplate(templateId, snapshot, restoredBy,
                    "Restored from version " + version.getVersion());
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore version: " + e.getMessage());
        }
    }

    // ── PRIVATE HELPERS ──────────────────────────────────────────────────────
    private void saveVersionSnapshot(ChecklistTemplate t, int version,
                                      TemplateDto dto, String note, String by) {
        try {
            String snapshot = objectMapper.writeValueAsString(dto);
            TemplateVersion v = new TemplateVersion();
            v.setTemplate(t);
            v.setVersion(version);
            v.setSnapshot(snapshot);
            v.setChangeNote(note);
            v.setCreatedBy(by);
            versionRepository.save(v);
        } catch (Exception e) {
            System.out.println("Snapshot save failed: " + e.getMessage());
        }
    }

    private TemplateDto toDto(ChecklistTemplate t) {
        TemplateDto dto = new TemplateDto();
        dto.setId(t.getId());
        dto.setTemplateName(t.getTemplateName());
        dto.setDepartment(t.getDepartment());
        dto.setVisibility(t.getVisibility());
        dto.setWorkflowType(t.getWorkflowType());
        dto.setDescription(t.getDescription());
        dto.setCreatedBy(t.getCreatedBy());
        dto.setCurrentVersion(t.getCurrentVersion());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        return dto;
    }
}