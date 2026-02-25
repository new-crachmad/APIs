package com.eventmaster.controller;

import com.eventmaster.model.Registration;
import com.eventmaster.repository.RegistrationRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    public RegistrationController(RegistrationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<Registration>> getAll() {
        logger.info("Buscando todas as inscrições");
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Registration> create(@Valid @RequestBody Registration registration) {
        Registration saved = repository.save(registration);
        logger.info("Inscrição criada com sucesso: ID {}", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(registration -> {
                    repository.delete(registration);
                    logger.info("Inscrição deletada com sucesso: ID {}", id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
