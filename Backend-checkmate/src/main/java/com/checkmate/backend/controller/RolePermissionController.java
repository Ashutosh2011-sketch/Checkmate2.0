package com.checkmate.backend.controller;

import com.checkmate.backend.dto.PermissionDto;
import com.checkmate.backend.dto.RoleDto;
import com.checkmate.backend.dto.UserPermissionDto;
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

    // ===== ROLE-LEVEL ENDPOINTS =====

    @GetMapping("/roles")
    public List<RoleDto> getAllRoles() {
        return service.getAllRoles();
    }

    @GetMapping("/roles/{id}")
    public RoleDto getRoleById(@PathVariable Long id) {
        return service.getRoleById(id);
    }

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody RoleDto roleDto) {
        try {
            return ResponseEntity.ok(service.createRole(roleDto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/roles/{id}/permissions")
    public ResponseEntity<?> updateRolePermissions(@PathVariable Long id,
                                                    @RequestBody List<PermissionDto> permissions) {
        try {
            return ResponseEntity.ok(service.updateRolePermissions(id, permissions));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            service.deleteRole(id);
            return ResponseEntity.ok(Map.of("message", "Role deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/permissions")
    public List<PermissionDto> getAllPermissions() {
        return service.getAllPermissions();
    }

    // ===== USER-LEVEL PERMISSION ENDPOINTS =====
    // Moved to /api/role-users and /api/user-permissions to avoid conflicts with UserController

    @GetMapping("/role-users/{roleName}")
    public List<UserPermissionDto> getUsersByRole(@PathVariable String roleName) {
        return service.getUsersByRole(roleName);
    }

    @GetMapping("/user-permissions/{userId}")
    public ResponseEntity<?> getUserPermissions(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(service.getUserPermissions(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/user-permissions/{userId}")
    public ResponseEntity<?> updateUserPermissions(@PathVariable Long userId,
                                                    @RequestBody List<PermissionDto> permissions) {
        try {
            return ResponseEntity.ok(service.updateUserPermissions(userId, permissions));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
