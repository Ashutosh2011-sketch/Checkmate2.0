package com.checkmate.backend.service;

import com.checkmate.backend.entity.Checklist;
import com.checkmate.backend.repository.ChecklistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChecklistService {

    private final ChecklistRepository repository;

    public ChecklistService(ChecklistRepository repository) {
        this.repository = repository;
    }

    public List<Checklist> getAll() {
        return repository.findAll();
    }

    public Checklist save(Checklist checklist) {
        return repository.save(checklist);
    }
}