package com.checkmate.backend.service;

import com.checkmate.backend.entity.UserAuthSession;
import com.checkmate.backend.repository.UserAuthSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserAuthSessionService {

    private final UserAuthSessionRepository repository;

    public UserAuthSessionService(UserAuthSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void recordLogin(String userEmail, String ipAddress) {
        UserAuthSession session = new UserAuthSession();
        session.setUserEmail(userEmail);
        session.setLoggedInAt(LocalDateTime.now());
        session.setIpAddress(ipAddress);
        repository.save(session);
    }

    @Transactional
    public void recordLogout(String userEmail) {
        repository.findFirstByUserEmailAndLoggedOutAtIsNullOrderByLoggedInAtDesc(userEmail)
                .ifPresent(session -> {
                    session.setLoggedOutAt(LocalDateTime.now());
                    repository.save(session);
                });
    }
}
