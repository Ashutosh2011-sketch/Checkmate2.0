
package com.checkmate.backend.service;

import com.checkmate.backend.entity.User;
import com.checkmate.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // CREATE USER (SAFE)
    public User createUser(User user) {

        // 🔥 prevent null tasks issue
        if (user.getTasks() == null) {
            user.setTasks(new ArrayList<>());
        }

        return repository.save(user);
    }

    // GET USER BY ID
    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // UPDATE USER (SAFE)
    public User updateUser(Long id, User updatedUser) {

        User existing = getUserById(id);

        existing.setName(updatedUser.getName());
        existing.setDepartment(updatedUser.getDepartment());
        existing.setRole(updatedUser.getRole());
        existing.setActive(updatedUser.isActive());

        // 🔥 prevent null crash
        if (updatedUser.getTasks() != null) {
            existing.setTasks(updatedUser.getTasks());
        } else {
            existing.setTasks(new ArrayList<>());
        }

        return repository.save(existing);
    }

    // DELETE USER
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    // ADD TASK
    public User addTask(Long id, String task) {

        User user = getUserById(id);

        if (user.getTasks() == null) {
            user.setTasks(new ArrayList<>());
        }

        user.getTasks().add(task);

        return repository.save(user);
    }

    // REMOVE TASK
    public User removeTask(Long id, String task) {

        User user = getUserById(id);

        if (user.getTasks() != null) {
            user.getTasks().remove(task);
        }

        return repository.save(user);
    }
}