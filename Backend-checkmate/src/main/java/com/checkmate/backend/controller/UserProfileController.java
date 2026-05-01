package com.checkmate.backend.controller;

import com.checkmate.backend.dto.ProfileUpdateRequest;
import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.repository.AppUserRepository;
import com.checkmate.backend.service.NotificationService;
import com.checkmate.backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")

@CrossOrigin(origins = "http://localhost:4200")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    /**
     * @param request   - DTO
     * @param principal - Current logged-in user identity
     */
    @PutMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileUpdateRequest request, Principal principal) {
        try {

            String currentUserEmail = principal.getName();

            userProfileService.updateUserDetails(currentUserEmail, request);

            // 2. 🔔 YAHAN TRIGGER KARO!
            AppUser user = userRepository.findByEmail(currentUserEmail).get();
            notificationService.createNotification(
                    user,
                    "Profile updated successfully",
                    "SUCCESS");

            return ResponseEntity.ok("Profile updated successfully!");

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<AppUser> getCurrentUser(Principal principal) {
        AppUser user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

}
