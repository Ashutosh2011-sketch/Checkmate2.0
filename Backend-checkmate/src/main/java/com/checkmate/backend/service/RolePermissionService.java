package com.checkmate.backend.service;

import com.checkmate.backend.dto.PermissionDto;
import com.checkmate.backend.dto.RoleDto;
import com.checkmate.backend.entity.Permission;
import com.checkmate.backend.entity.Role;
import com.checkmate.backend.entity.RolePermission;
import com.checkmate.backend.repository.PermissionRepository;
import com.checkmate.backend.repository.RolePermissionRepository;
import com.checkmate.backend.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionService(RoleRepository roleRepository,
                                  PermissionRepository permissionRepository,
                                  RolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    // ===== GET ALL ROLES WITH PERMISSIONS =====
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toRoleDto)
                .collect(Collectors.toList());
    }

    // ===== GET SINGLE ROLE =====
    @Transactional(readOnly = true)
    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return toRoleDto(role);
    }

    // ===== CREATE ROLE =====
    @Transactional
    public RoleDto createRole(RoleDto dto) {
        if (roleRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Role with name '" + dto.getName() + "' already exists!");
        }

        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        Role savedRole = roleRepository.save(role);

        // Create permission mappings
        if (dto.getPermissions() != null) {
            List<Permission> allPermissions = permissionRepository.findAll();

            for (PermissionDto permDto : dto.getPermissions()) {
                Permission permission = allPermissions.stream()
                        .filter(p -> p.getId().equals(permDto.getId()))
                        .findFirst()
                        .orElse(null);

                if (permission != null) {
                    RolePermission rp = new RolePermission();
                    rp.setRole(savedRole);
                    rp.setPermission(permission);
                    rp.setEnabled(permDto.isEnabled());
                    rolePermissionRepository.save(rp);
                }
            }
        } else {
            // If no permissions provided, create entries for all permissions (all disabled)
            List<Permission> allPermissions = permissionRepository.findAll();
            for (Permission permission : allPermissions) {
                RolePermission rp = new RolePermission();
                rp.setRole(savedRole);
                rp.setPermission(permission);
                rp.setEnabled(false);
                rolePermissionRepository.save(rp);
            }
        }

        // Reload to get permissions
        return toRoleDto(roleRepository.findById(savedRole.getId()).get());
    }

    // ===== UPDATE ROLE PERMISSIONS =====
    @Transactional
    public RoleDto updateRolePermissions(Long roleId, List<PermissionDto> permissions) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        List<RolePermission> existingMappings = role.getRolePermissions();

        for (PermissionDto permDto : permissions) {
            RolePermission mapping = existingMappings.stream()
                    .filter(rp -> rp.getPermission().getId().equals(permDto.getId()))
                    .findFirst()
                    .orElse(null);

            if (mapping != null) {
                mapping.setEnabled(permDto.isEnabled());
                rolePermissionRepository.save(mapping);
            } else {
                // Create new mapping if doesn't exist
                Permission permission = permissionRepository.findById(permDto.getId())
                        .orElse(null);
                if (permission != null) {
                    RolePermission rp = new RolePermission();
                    rp.setRole(role);
                    rp.setPermission(permission);
                    rp.setEnabled(permDto.isEnabled());
                    rolePermissionRepository.save(rp);
                }
            }
        }

        return toRoleDto(roleRepository.findById(roleId).get());
    }

    // ===== DELETE ROLE =====
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    // ===== GET ALL PERMISSIONS =====
    @Transactional(readOnly = true)
    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionDto)
                .collect(Collectors.toList());
    }

    // ===== HELPER: Role -> RoleDto =====
    private RoleDto toRoleDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());

        List<PermissionDto> permDtos = new ArrayList<>();
        if (role.getRolePermissions() != null) {
            for (RolePermission rp : role.getRolePermissions()) {
                PermissionDto permDto = new PermissionDto();
                permDto.setId(rp.getPermission().getId());
                permDto.setName(rp.getPermission().getName());
                permDto.setCategory(rp.getPermission().getCategory());
                permDto.setEnabled(rp.isEnabled());
                permDtos.add(permDto);
            }
        }
        dto.setPermissions(permDtos);
        return dto;
    }

    // ===== HELPER: Permission -> PermissionDto (without enabled) =====
    private PermissionDto toPermissionDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setCategory(permission.getCategory());
        dto.setEnabled(false); // default
        return dto;
    }
}
