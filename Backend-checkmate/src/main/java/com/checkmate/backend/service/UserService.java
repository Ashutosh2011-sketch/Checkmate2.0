
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

<<<<<<< HEAD
    // CREATE USER (SAFE)
=======
    // CREATE USER
>>>>>>> 0656e46df790ebd500c5fb92b29f19364d250cc4
    public User createUser(User user) {
        return repository.save(user);
    }

<<<<<<< HEAD
    // GET USER BY ID
=======
    //  GET USER BY ID
>>>>>>> 0656e46df790ebd500c5fb92b29f19364d250cc4
    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

<<<<<<< HEAD
    // UPDATE USER (SAFE)
=======
    // UPDATE USER
>>>>>>> 0656e46df790ebd500c5fb92b29f19364d250cc4
    public User updateUser(Long id, User updatedUser) {

        User existing = getUserById(id);

        existing.setName(updatedUser.getName());
        existing.setDepartment(updatedUser.getDepartment());
        existing.setRole(updatedUser.getRole());
        existing.setActive(updatedUser.isActive());

        return repository.save(existing);
    }

<<<<<<< HEAD
    // DELETE USER
=======
    //  DELETE USER
>>>>>>> 0656e46df790ebd500c5fb92b29f19364d250cc4
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

<<<<<<< HEAD
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
=======
    //  FIXED: GET TASKS FROM DB (NO DATA LOSS)
    public List<String> getTasksForUser(String userName) {

        // Direct DB query → reliable
        return taskRepository.findTasksByUserName(userName);
>>>>>>> 0656e46df790ebd500c5fb92b29f19364d250cc4
    }
}