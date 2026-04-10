package com.checkmate.backend.service;

import com.checkmate.backend.dto.ProfileUpdateRequest;
import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void updateUserDetails(String email, ProfileUpdateRequest request) {

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {

            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new RuntimeException("Validation Failed: Please enter your Current Password.");
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Security Error: The current password you entered is incorrect.");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setName(request.getFullName());
        }

        if (request.getJobTitle() != null && !request.getJobTitle().trim().isEmpty()) {
            user.setDesignation(request.getJobTitle());
        }

        if (request.getDepartment() != null && !request.getDepartment().trim().isEmpty()) {
            user.setDepartment(request.getDepartment());
        }

        userRepository.save(user);
    }
}