package com.checkmate.backend.controller;

import com.checkmate.backend.entity.User;
import com.checkmate.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // GET ALL
    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    // CREATE
    @PostMapping
    public User createUser(@RequestBody User user) {
        return service.createUser(user);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return service.getUserById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return service.updateUser(id, user);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return "User deleted";
    }

    // ADD TASK
    @PostMapping("/{id}/tasks")
    public User addTask(@PathVariable Long id, @RequestBody String task) {
        return service.addTask(id, task);
    }

    // REMOVE TASK
    @DeleteMapping("/{id}/tasks")
    public User removeTask(@PathVariable Long id, @RequestBody String task) {
        return service.removeTask(id, task);
    }
}