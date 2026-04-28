package com.checkmate.backend.controller;

import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.repository.AppUserRepository;

import com.checkmate.backend.entity.User;
import com.checkmate.backend.repository.UserRepository;

import com.checkmate.backend.entity.Role;
import com.checkmate.backend.entity.RolePermission;
import com.checkmate.backend.repository.RoleRepository;

import com.checkmate.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private UserRepository teammateUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AppUser appUser) {

        if (appUser.getName() == null || appUser.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name is required!"));
        }

        // 1. Save Original Role to Designation
        String originalRoleFromFrontend = appUser.getRole();
        if (appUser.getRole() != null && !appUser.getRole().isEmpty()) {
            appUser.setDesignation(appUser.getRole());
        }

        appUser.setRole("USER");

        String cleanName = appUser.getName().trim().replaceAll("\\s+", "").toLowerCase();
        String generatedEmail = cleanName + "@checkmate.com";
        String generatedPassword = generateRandomPassword();

        appUser.setEmail(generatedEmail);
        appUser.setPassword(passwordEncoder.encode(generatedPassword));

        if (appUserRepository.findByEmail(generatedEmail).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Employee with this name already exists!"));
        }

        appUserRepository.save(appUser);

        // Also create a matching record in the users (teammates) table
        try {
            User teammateUser = new User();
            teammateUser.setName(appUser.getName());
            teammateUser.setDepartment(appUser.getDepartment());
            teammateUser.setRole(originalRoleFromFrontend);
            teammateUser.setEmail(generatedEmail); // CRITICAL: Set email for cross-table sync
            teammateUser.setActive(true);
            teammateUserRepository.save(teammateUser);
        } catch (Exception e) {
            System.out.println("Teammate save error: " + e.getMessage());
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "User created successfully!");
        response.put("email", generatedEmail);
        response.put("password", generatedPassword);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {

        String email = loginData.get("email");
        String password = loginData.get("password");

        try {
            System.out.println("LOGIN-DEBUG: Attempting login for email='" + email + "'");
            // Check if user exists first
            var userOpt = appUserRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                AppUser u = userOpt.get();
                System.out.println("LOGIN-DEBUG: User found: name='" + u.getName() + "' role='" + u.getRole() + "' pwHash='" + (u.getPassword() != null ? u.getPassword().substring(0, Math.min(20, u.getPassword().length())) : "NULL") + "...'");
                System.out.println("LOGIN-DEBUG: Password matches? " + passwordEncoder.matches(password, u.getPassword()));
            } else {
                System.out.println("LOGIN-DEBUG: No user found with email '" + email + "'");
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (Exception e) {
            System.out.println("LOGIN-DEBUG: Auth failed: " + e.getClass().getName() + " - " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Email or Password!"));
        }

        AppUser user = appUserRepository.findByEmail(email).get();
        String token = jwtUtil.generateToken(email, user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("message", "Login Successful!");
        response.put("name", user.getName());

        // Return the user's designation (which is the role name from roles table)
        String designation = user.getDesignation();
        response.put("designation", designation != null ? designation : "");

        // Look up actual permissions for this user's designation (role name)
        List<String> permissions = new ArrayList<>();
        if (designation != null && !designation.isEmpty()) {
            Optional<Role> roleOpt = roleRepository.findByName(designation);
            if (roleOpt.isPresent()) {
                Role role = roleOpt.get();
                permissions = role.getRolePermissions().stream()
                        .filter(RolePermission::isEnabled)
                        .map(rp -> rp.getPermission().getName())
                        .collect(Collectors.toList());
            }
        }

        // ADMIN gets all permissions automatically
        if ("ADMIN".equals(user.getRole())) {
            permissions = List.of(
                "Create Checklists", "Publish Workflows", "Manage Users",
                "Access Audit Logs", "View All Reports", "Manage Workflows"
            );
        }

        response.put("permissions", permissions);

        return ResponseEntity.ok(response);
    }
}
