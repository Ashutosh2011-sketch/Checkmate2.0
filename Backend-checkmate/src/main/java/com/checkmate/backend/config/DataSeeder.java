package com.checkmate.backend.config;

import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.entity.Permission;
import com.checkmate.backend.entity.Role;
import com.checkmate.backend.entity.RolePermission;
import com.checkmate.backend.repository.AppUserRepository;
import com.checkmate.backend.repository.PermissionRepository;
import com.checkmate.backend.repository.RolePermissionRepository;
import com.checkmate.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private AppUserRepository userRepository;

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

        // 1. Seed admin user
        if (userRepository.count() == 0) {
            AppUser admin = new AppUser();
            admin.setEmail("admin@checkmate.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("First Admin user created successfully in the database!");
        } else {
            System.out.println("Users already exist in the database, skipping creation.");
        }

        // 2. Seed default permissions
        if (permissionRepository.count() == 0) {
            Permission p1 = createPermission("Create Checklists", "Checklist Permissions");
            Permission p2 = createPermission("Publish Workflows", "Checklist Permissions");
            Permission p3 = createPermission("Manage Users", "User Management Permissions");
            Permission p4 = createPermission("Access Audit Logs", "User Management Permissions");
            Permission p5 = createPermission("View All Reports", "Reporting Permissions");
            Permission p6 = createPermission("Manage Workflows", "Workflow Permissions");

            List<Permission> allPerms = List.of(p1, p2, p3, p4, p5, p6);
            permissionRepository.saveAll(allPerms);
            System.out.println("Default permissions seeded successfully!");

            // 3. Seed default roles with permissions
            if (roleRepository.count() == 0) {
                // Approver: all except Access Audit Logs
                Role approver = new Role();
                approver.setName("Approver");
                approver.setDescription("Can approve checklists and manage workflows");
                roleRepository.save(approver);
                Map<Permission, Boolean> approverPerms = Map.of(
                    p1, true, p2, true, p3, true, p4, false, p5, true, p6, true
                );
                seedRolePermissions(approver, allPerms, approverPerms);

                // Reviewer: Create Checklists + View Reports
                Role reviewer = new Role();
                reviewer.setName("Reviewer");
                reviewer.setDescription("Can review checklists and view reports");
                roleRepository.save(reviewer);
                Map<Permission, Boolean> reviewerPerms = Map.of(
                    p1, true, p2, false, p3, false, p4, false, p5, true, p6, false
                );
                seedRolePermissions(reviewer, allPerms, reviewerPerms);

                // Executor: Create Checklists only
                Role executor = new Role();
                executor.setName("Executor");
                executor.setDescription("Can execute assigned checklists");
                roleRepository.save(executor);
                Map<Permission, Boolean> executorPerms = Map.of(
                    p1, true, p2, false, p3, false, p4, false, p5, false, p6, false
                );
                seedRolePermissions(executor, allPerms, executorPerms);

                System.out.println("Default roles seeded successfully!");
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
