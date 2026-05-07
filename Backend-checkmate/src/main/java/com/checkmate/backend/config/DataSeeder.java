package com.checkmate.backend.config;

import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.entity.Permission;
import com.checkmate.backend.entity.Role;
import com.checkmate.backend.entity.RolePermission;
import com.checkmate.backend.entity.User;
import com.checkmate.backend.repository.AppUserRepository;
import com.checkmate.backend.repository.PermissionRepository;
import com.checkmate.backend.repository.RolePermissionRepository;
import com.checkmate.backend.repository.RoleRepository;
import com.checkmate.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public void run(String... args) throws Exception {

        // 1. Seed admin user if DB is empty
        if (appUserRepository.count() == 0) {
            AppUser admin = new AppUser();
            admin.setName("Admin User");
            admin.setEmail("admin@checkmate.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setDesignation("Approver");
            appUserRepository.save(admin);
            System.out.println("Admin user created successfully!");
        }

        // 2. Sync: Ensure every AppUser has a matching User record in the users table
        for (AppUser appUser : appUserRepository.findAll()) {
            if (appUser.getEmail() != null) {
                boolean userExists = userRepository.findAll().stream()
                        .anyMatch(u -> appUser.getEmail().equals(u.getEmail()));
                if (!userExists) {
                    User teammateUser = new User();
                    teammateUser.setName(appUser.getName() != null ? appUser.getName() : appUser.getEmail());
                    teammateUser.setDepartment(appUser.getDepartment() != null ? appUser.getDepartment() : "General");
                    teammateUser.setRole(appUser.getDesignation() != null ? appUser.getDesignation() : "USER");
                    teammateUser.setEmail(appUser.getEmail());
                    teammateUser.setActive(true);
                    userRepository.save(teammateUser);
                    System.out.println("Synced User record for: " + appUser.getEmail());
                }
            }
        }

        // 2.5 REVERSE SYNC: Ensure every User record has a matching AppUser for login
        for (User user : userRepository.findAll()) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                boolean appUserExists = appUserRepository.findByEmail(user.getEmail()).isPresent();
                if (!appUserExists) {
                    String name = user.getEmail().split("@")[0];
                    String plainPassword = name + "234";
                    AppUser newAppUser = new AppUser();
                    newAppUser.setName(user.getName());
                    newAppUser.setEmail(user.getEmail());
                    newAppUser.setPassword(passwordEncoder.encode(plainPassword));
                    newAppUser.setRole("USER");
                    newAppUser.setDepartment(user.getDepartment());
                    newAppUser.setDesignation(user.getRole());
                    appUserRepository.save(newAppUser);
                    System.out.println(
                            "Created AppUser for login: " + user.getEmail() + " -> password: " + plainPassword);
                }
            }
        }

        // 4. Sync: Ensure every User record has a matching designation in AppUser
        for (User user : userRepository.findAll()) {
            if (user.getEmail() != null && user.getRole() != null) {
                appUserRepository.findByEmail(user.getEmail()).ifPresent(appUser -> {
                    if (appUser.getDesignation() == null || appUser.getDesignation().isEmpty()) {
                        appUser.setDesignation(user.getRole());
                        appUserRepository.save(appUser);
                        System.out.println("Synced designation for: " + appUser.getEmail() + " -> " + user.getRole());
                    }
                });
            }
        }

        // 4. Seed default permissions
        if (permissionRepository.count() == 0) {
            Permission p1 = createPermission("Create Checklists", "Checklist Permissions");
            Permission p2 = createPermission("Publish Workflows", "Checklist Permissions");
            Permission p3 = createPermission("Manage Users", "User Management Permissions");
            Permission p4 = createPermission("Access Audit Logs", "User Management Permissions");
            Permission p5 = createPermission("View All Reports", "Reporting Permissions");
            Permission p6 = createPermission("Manage Workflows", "Workflow Permissions");

            List<Permission> allPerms = List.of(p1, p2, p3, p4, p5, p6);
            permissionRepository.saveAll(allPerms);
            System.out.println("Default permissions seeded!");

            // 5. Seed default roles with permissions
            if (roleRepository.count() == 0) {
                // Approver: all except Access Audit Logs
                Role approver = new Role();
                approver.setName("Approver");
                approver.setDescription("Can approve checklists and manage workflows");
                roleRepository.save(approver);
                seedRolePermissions(approver, allPerms, Map.of(
                        p1, true, p2, true, p3, true, p4, false, p5, true, p6, true));

                // Reviewer: Create Checklists + View Reports
                Role reviewer = new Role();
                reviewer.setName("Reviewer");
                reviewer.setDescription("Can review checklists and view reports");
                roleRepository.save(reviewer);
                seedRolePermissions(reviewer, allPerms, Map.of(
                        p1, true, p2, false, p3, false, p4, false, p5, true, p6, false));

                // Executor: Create Checklists only
                Role executor = new Role();
                executor.setName("Executor");
                executor.setDescription("Can execute assigned checklists");
                roleRepository.save(executor);
                seedRolePermissions(executor, allPerms, Map.of(
                        p1, true, p2, false, p3, false, p4, false, p5, false, p6, false));

                System.out.println("Default roles seeded!");
            }
        } else {
            System.out.println("Permissions already exist, skipping seeding.");
        }
    }

    private Permission createPermission(String name, String category) {
        Permission p = new Permission();
        p.setName(name);
        p.setCategory(category);
        return p;
    }

    private void seedRolePermissions(Role role, List<Permission> allPerms, Map<Permission, Boolean> enabledMap) {
        for (Permission perm : allPerms) {
            RolePermission rp = new RolePermission();
            rp.setRole(role);
            rp.setPermission(perm);
            rp.setEnabled(enabledMap.getOrDefault(perm, false));
            rolePermissionRepository.save(rp);
        }
    }
}
