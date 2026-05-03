package com.checkmate.backend.repository;

import com.checkmate.backend.entity.TemplateVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, Long> {
    List<TemplateVersion> findByTemplateIdOrderByVersionDesc(Long templateId);
}