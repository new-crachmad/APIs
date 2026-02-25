package com.eventmaster.controller;

import com.eventmaster.dto.EventRequestDTO;
import com.eventmaster.dto.EventResponseDTO;
import com.eventmaster.service.EventService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    public EventController(EventService service) {
        this.service = service;
    }

    // Listar todos os eventos
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAll() {
        logger.info("Buscando todos os eventos");
        return ResponseEntity.ok(service.findAll());
    }

    // Buscar evento por ID
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Criar novo evento
    @PostMapping
    public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventRequestDTO dto) {
        EventResponseDTO savedEvent = service.create(dto);
        logger.info("Evento criado com sucesso: ID {}", savedEvent.id());
        return ResponseEntity.ok(savedEvent);
    }

    // Deletar evento
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        service.delete(id);
        logger.info("Evento deletado com sucesso: ID {}", id);
        return ResponseEntity.noContent().build();
    }
}