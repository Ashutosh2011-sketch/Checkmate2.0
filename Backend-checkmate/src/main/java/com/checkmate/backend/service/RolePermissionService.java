package com.checkmate.backend.service;

import com.checkmate.backend.dto.PermissionDto;
import com.checkmate.backend.dto.RoleDto;
import com.checkmate.backend.dto.UserPermissionDto;
import com.checkmate.backend.entity.*;
import com.checkmate.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AppUserRepository appUserRepository;
    private final UserPermissionRepository userPermissionRepository;

    public RolePermissionService(RoleRepository roleRepository,
                                  PermissionRepository permissionRepository,
                                  RolePermissionRepository rolePermissionRepository,
                                  AppUserRepository appUserRepository,
                                  UserPermissionRepository userPermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.appUserRepository = appUserRepository;
        this.userPermissionRepository = userPermissionRepository;
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

        if (dto.getPermissions() != null) {
            List<Permission> allPermissions = permissionRepository.findAll();
            for (PermissionDto permDto : dto.getPermissions()) {
                Permission permission = allPermissions.stream()
                        .filter(p -> p.getId().equals(permDto.getId()))
                        .findFirst().orElse(null);
                if (permission != null) {
                    RolePermission rp = new RolePermission();
                    rp.setRole(savedRole);
                    rp.setPermission(permission);
                    rp.setEnabled(permDto.isEnabled());
                    rolePermissionRepository.save(rp);
                }
            }
        } else {
            List<Permission> allPermissions = permissionRepository.findAll();
            for (Permission permission : allPermissions) {
                RolePermission rp = new RolePermission();
                rp.setRole(savedRole);
                rp.setPermission(permission);
                rp.setEnabled(false);
                rolePermissionRepository.save(rp);
            }
        }

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
                    .findFirst().orElse(null);

            if (mapping != null) {
                mapping.setEnabled(permDto.isEnabled());
                rolePermissionRepository.save(mapping);
            } else {
                Permission permission = permissionRepository.findById(permDto.getId()).orElse(null);
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

    // ===== GET USERS BY ROLE (designation) =====
    @Transactional(readOnly = true)
    public List<UserPermissionDto> getUsersByRole(String roleName) {
        List<AppUser> users = appUserRepository.findByDesignation(roleName);
        return users.stream().map(u -> {
            UserPermissionDto dto = new UserPermissionDto(u.getId(), u.getName(), u.getEmail(), u.getDesignation());
            // no permissions loaded here — just the user list
            return dto;
        }).collect(Collectors.toList());
    }

    // ===== GET USER-SPECIFIC PERMISSIONS =====
    // Returns the merged permissions: role defaults + user overrides
    @Transactional(readOnly = true)
    public UserPermissionDto getUserPermissions(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserPermissionDto dto = new UserPermissionDto(user.getId(), user.getName(), user.getEmail(), user.getDesignation());

        // 1. Get role-level permissions as defaults
        List<PermissionDto> mergedPermissions = new ArrayList<>();
        if (user.getDesignation() != null && !user.getDesignation().isEmpty()) {
            Optional<Role> roleOpt = roleRepository.findByName(user.getDesignation());
            if (roleOpt.isPresent()) {
                for (RolePermission rp : roleOpt.get().getRolePermissions()) {
                    PermissionDto pd = new PermissionDto();
                    pd.setId(rp.getPermission().getId());
                    pd.setName(rp.getPermission().getName());
                    pd.setCategory(rp.getPermission().getCategory());
                    pd.setEnabled(rp.isEnabled());
                    mergedPermissions.add(pd);
                }
            }
        }

        // If no role permissions found, start with all permissions disabled
        if (mergedPermissions.isEmpty()) {
            for (Permission p : permissionRepository.findAll()) {
                PermissionDto pd = new PermissionDto();
                pd.setId(p.getId());
                pd.setName(p.getName());
                pd.setCategory(p.getCategory());
                pd.setEnabled(false);
                mergedPermissions.add(pd);
            }
        }

        // 2. Apply user-specific overrides
        List<UserPermission> userOverrides = userPermissionRepository.findByUserId(userId);
        for (UserPermission up : userOverrides) {
            mergedPermissions.stream()
                    .filter(pd -> pd.getId().equals(up.getPermission().getId()))
                    .findFirst()
                    .ifPresent(pd -> pd.setEnabled(up.isEnabled()));
        }

        dto.setPermissions(mergedPermissions);
        return dto;
    }

    // ===== UPDATE USER-SPECIFIC PERMISSIONS =====
    @Transactional
    public UserPermissionDto updateUserPermissions(Long userId, List<PermissionDto> permissions) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get role defaults for comparison
        Map<Long, Boolean> roleDefaults = new HashMap<>();
        if (user.getDesignation() != null) {
            Optional<Role> roleOpt = roleRepository.findByName(user.getDesignation());
            if (roleOpt.isPresent()) {
                for (RolePermission rp : roleOpt.get().getRolePermissions()) {
                    roleDefaults.put(rp.getPermission().getId(), rp.isEnabled());
                }
            }
        }

        List<UserPermission> existing = userPermissionRepository.findByUserId(userId);

        for (PermissionDto pd : permissions) {
            boolean roleDefault = roleDefaults.getOrDefault(pd.getId(), false);

            if (pd.isEnabled() == roleDefault) {
                // Same as role default → remove override if exists
                existing.stream()
                        .filter(up -> up.getPermission().getId().equals(pd.getId()))
                        .findFirst()
                        .ifPresent(up -> userPermissionRepository.delete(up));
            } else {
                // Different from role default → save override
                UserPermission up = existing.stream()
                        .filter(u -> u.getPermission().getId().equals(pd.getId()))
                        .findFirst()
                        .orElseGet(() -> {
                            UserPermission newUp = new UserPermission();
                            newUp.setUserId(userId);
                            newUp.setPermission(permissionRepository.findById(pd.getId()).get());
                            return newUp;
                        });
                up.setEnabled(pd.isEnabled());
                userPermissionRepository.save(up);
            }
        }

        return getUserPermissions(userId);
    }

    // ===== GET EFFECTIVE PERMISSIONS FOR LOGIN =====
    // Returns the final merged list of permission names for a user
    @Transactional(readOnly = true)
    public List<String> getEffectivePermissions(Long userId, String designation) {
        // 1. Start with role defaults
        Map<Long, Boolean> permMap = new LinkedHashMap<>();
        Map<Long, String> permNames = new LinkedHashMap<>();

        if (designation != null && !designation.isEmpty()) {
            Optional<Role> roleOpt = roleRepository.findByName(designation);
            if (roleOpt.isPresent()) {
                for (RolePermission rp : roleOpt.get().getRolePermissions()) {
                    permMap.put(rp.getPermission().getId(), rp.isEnabled());
                    permNames.put(rp.getPermission().getId(), rp.getPermission().getName());
                }
            }
        }

        // 2. Apply user-specific overrides
        List<UserPermission> overrides = userPermissionRepository.findByUserId(userId);
        for (UserPermission up : overrides) {
            permMap.put(up.getPermission().getId(), up.isEnabled());
            permNames.put(up.getPermission().getId(), up.getPermission().getName());
        }

        // 3. Return only enabled permission names
        return permMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(e -> permNames.get(e.getKey()))
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

    // ===== HELPER: Permission -> PermissionDto =====
    private PermissionDto toPermissionDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setCategory(permission.getCategory());
        dto.setEnabled(false);
        return dto;
    }
}
