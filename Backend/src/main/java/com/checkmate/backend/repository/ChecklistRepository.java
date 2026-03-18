package com.checkmate.backend.repository;

import com.checkmate.backend.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    // Ye interface khali rahega, JpaRepository saara kaam sambhaal lega.
}   