
package com.checkmate.backend.service;

import com.checkmate.backend.entity.User;
import com.checkmate.backend.repository.UserRepository;
import com.checkmate.backend.repository.ChecklistRepository;
import com.checkmate.backend.repository.TaskRepository; // 
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final ChecklistRepository checklistRepository;
    private final TaskRepository taskRepository; //

    public UserService(UserRepository repository,
            ChecklistRepository checklistRepository,
            TaskRepository taskRepository) {
        this.repository = repository;
        this.checklistRepository = checklistRepository;
        this.taskRepository = taskRepository;
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // CREATE USER
    public User createUser(User user) {
        return repository.save(user);
    }

    // GET USER BY ID
    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // UPDATE USER
    public User updateUser(Long id, User updatedUser) {

        User existing = getUserById(id);

        existing.setName(updatedUser.getName());
        existing.setDepartment(updatedUser.getDepartment());
        existing.setRole(updatedUser.getRole());
        existing.setActive(updatedUser.isActive());

        return repository.save(existing);
    }

    // DELETE USER
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    // FIXED: GET TASKS FROM DB (NO DATA LOSS)
    public List<String> getTasksForUser(String userName) {

        // Direct DB query → reliable
        return taskRepository.findTasksByUserName(userName);
    }
}