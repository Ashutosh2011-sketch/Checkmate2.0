package com.checkmate.backend.config;

import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

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
    }
}
