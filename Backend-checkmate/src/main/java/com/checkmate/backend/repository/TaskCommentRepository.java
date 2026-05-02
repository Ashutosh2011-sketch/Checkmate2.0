package com.checkmate.backend.repository;

import com.checkmate.backend.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    long countByTaskId(Long taskId);
}
