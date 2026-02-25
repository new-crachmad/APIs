package com.eventmaster.controller;

import com.eventmaster.dto.RegistrationRequestDTO;
import com.eventmaster.dto.RegistrationResponseDTO;
import com.eventmaster.service.RegistrationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService service;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RegistrationResponseDTO>> getAll() {
        logger.info("Buscando todas as inscrições");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<RegistrationResponseDTO> create(@Valid @RequestBody RegistrationRequestDTO dto) {
        RegistrationResponseDTO saved = service.create(dto);
        logger.info("Inscrição criada com sucesso: ID {}", saved.id());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        service.delete(id);
        logger.info("Inscrição deletada com sucesso: ID {}", id);
        return ResponseEntity.noContent().build();
    }
}
