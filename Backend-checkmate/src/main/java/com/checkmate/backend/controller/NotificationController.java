package com.checkmate.backend.controller;

import com.checkmate.backend.entity.Notification;
import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.repository.AppUserRepository;
import com.checkmate.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AppUserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyNotifications(Principal principal) {
        // 1. Logged-in user ka email nikalna
        String email = principal.getName();
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Us user ki saari notifications fetch karna
        // List<Notification> notifications =
        // notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        // return ResponseEntity.ok(notifications);
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());

        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    @Transactional
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Security Check: Kya ye notification usi user ki hai jo login hai?
        if (!notification.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(403).build(); // Dusre ki notification read nahi kar sakte
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }
}