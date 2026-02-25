package com.eventmaster.controller;

import com.eventmaster.model.Event;
import com.eventmaster.repository.EventRepository;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    public EventController(EventRepository repository) {
        this.repository = repository;
    }

    // Listar todos os eventos
    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        logger.info("Buscando todos os eventos");
        List<Event> events = repository.findAll();
        return ResponseEntity.ok(events);
    }

    // Buscar evento por ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar novo evento
    @PostMapping
    public ResponseEntity<Event> create(@Valid @RequestBody Event event) {
        Event savedEvent = repository.save(event);
        logger.info("Evento criado com sucesso: ID {}", savedEvent.getId());
        return ResponseEntity.ok(savedEvent);
    }

    // Atualizar evento
    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id, @Valid @RequestBody Event event) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(event.getTitle());
                    existing.setDescription(event.getDescription());
                    existing.setStartAt(event.getStartAt());
                    existing.setCategory(event.getCategory());
                    existing.setOrganizer(event.getOrganizer());

                    Event updated = repository.save(existing);
                    logger.info("Evento atualizado com sucesso: ID {}", id);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Deletar evento
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(event -> {
                    repository.delete(event);
                    logger.info("Evento deletado com sucesso: ID {}", id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}