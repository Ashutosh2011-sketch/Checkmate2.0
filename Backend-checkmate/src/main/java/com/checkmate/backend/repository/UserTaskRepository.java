package com.checkmate.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.checkmate.backend.entity.UserTask;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    @Query("SELECT ut.task FROM UserTask ut WHERE ut.user.id = :userId")
    List<String> findTasksByUserId(@Param("userId") Long userId);

    List<UserTask> findByUserId(Long userId);

    void deleteByUserIdAndTask(Long userId, String task);
}
