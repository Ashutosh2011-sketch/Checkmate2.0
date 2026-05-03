package com.checkmate.backend.repository;

import com.checkmate.backend.entity.ChecklistTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TemplateRepository extends JpaRepository<ChecklistTemplate, Long> {
    List<ChecklistTemplate> findByDepartment(String department);
    List<ChecklistTemplate> findByVisibility(String visibility);
    List<ChecklistTemplate> findByCreatedBy(String createdBy);
    List<ChecklistTemplate> findAllByOrderByUpdatedAtDesc();
}