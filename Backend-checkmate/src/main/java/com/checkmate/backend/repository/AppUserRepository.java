package com.checkmate.backend.repository;

import com.checkmate.backend.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByName(String name);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByRole(String role);

    List<AppUser> findByDesignation(String designation);
}
