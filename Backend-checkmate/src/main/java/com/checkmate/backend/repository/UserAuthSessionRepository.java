package com.checkmate.backend.repository;

import com.checkmate.backend.entity.UserAuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthSessionRepository extends JpaRepository<UserAuthSession, Long> {

    Optional<UserAuthSession> findFirstByUserEmailAndLoggedOutAtIsNullOrderByLoggedInAtDesc(String userEmail);
}
