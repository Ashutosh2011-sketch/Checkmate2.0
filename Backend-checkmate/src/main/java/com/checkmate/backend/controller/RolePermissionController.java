package com.checkmate.backend.controller;

import com.checkmate.backend.dto.PermissionDto;
import com.checkmate.backend.dto.RoleDto;
import com.checkmate.backend.service.RolePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class RolePermissionController {

    private final RolePermissionService service;

    public RolePermissionController(RolePermissionService service) {
        this.service = service;
    }

    // ===== GET ALL ROLES =====
    @GetMapping("/roles")
    public List<RoleDto> getAllRoles() {
        return service.getAllRoles();
    }

    // ===== GET SINGLE ROLE =====
    @GetMapping("/roles/{id}")
    public RoleDto getRoleById(@PathVariable Long id) {
        return service.getRoleById(id);
    }

    // ===== CREATE ROLE =====
    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody RoleDto roleDto) {
        try {
            RoleDto created = service.createRole(roleDto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== UPDATE ROLE PERMISSIONS =====
    @PutMapping("/roles/{id}/permissions")
    public ResponseEntity<?> updateRolePermissions(@PathVariable Long id,
                                                    @RequestBody List<PermissionDto> permissions) {
        try {
            RoleDto updated = service.updateRolePermissions(id, permissions);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== DELETE ROLE =====
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            service.deleteRole(id);
            return ResponseEntity.ok(Map.of("message", "Role deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== GET ALL AVAILABLE PERMISSIONS =====
    @GetMapping("/permissions")
    public List<PermissionDto> getAllPermissions() {
        return service.getAllPermissions();
    }
}
