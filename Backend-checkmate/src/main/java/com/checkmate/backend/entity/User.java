package com.checkmate.backend.entity;

import jakarta.persistence.*;

@Entity
<<<<<<< HEAD
@Table(name = "users") // FIX: avoid reserved keyword
=======
@Table(name = "users")
>>>>>>> 0656e46df790ebd500c5fb92b29f19364d250cc4
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String department;
    private String role;
    private String email;
    private boolean active;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public String getEmail() { return email; }
    
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}