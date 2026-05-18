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
@CrossOrigin(origins = "${app.cors.allowed-origin:http://localhost:4200}")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AppUserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyNotifications(Principal principal) {

        String email = principal.getName();
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());

        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    @Transactional
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Notification>> getAdminNotifications(Principal principal) {
        AppUser user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());

        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/admin/mark-all-read")
    @Transactional
    public ResponseEntity<Void> markAllAdminRead(Principal principal) {
        AppUser user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());

        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
        return ResponseEntity.ok().build();
    }
}