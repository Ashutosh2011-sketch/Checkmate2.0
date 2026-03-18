package com.checkmate.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity 
@Table(name = "checklists") // Database mein table ka naam automatic ban jayega
@Data // Isse Getter/Setter likhne ki zaroorat nahi padegi
public class Checklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private boolean completed;
}
