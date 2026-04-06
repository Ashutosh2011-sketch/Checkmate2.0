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

    // ✅ GET ALL USERS
    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    // ✅ CREATE USER
    @PostMapping
    public User createUser(@RequestBody User user) {
        return service.createUser(user);
    }

    // ✅ GET USER BY ID
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return service.getUserById(id);
    }

    // ✅ UPDATE USER
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return service.updateUser(id, user);
    }

    // ✅ DELETE USER
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return "User deleted";
    }

    // 🔥 NEW: GET TASKS FROM CHECKLIST
    @GetMapping("/{name}/tasks")
    public List<String> getTasksForUser(@PathVariable String name) {
        return service.getTasksForUser(name);
    }
}