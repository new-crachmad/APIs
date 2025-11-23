package com.eventmaster.controller;

import com.eventmaster.model.Event;
import com.eventmaster.repository.EventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository repository;

    public EventController(EventRepository repository) {
        this.repository = repository;
    }

    // Listar todos os eventos
    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
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
    public ResponseEntity<Event> create(@RequestBody Event event) {
        Event savedEvent = repository.save(event);
        return ResponseEntity.ok(savedEvent);
    }

    // Atualizar evento
    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event event) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(event.getName());
                    existing.setDescription(event.getDescription());
                    existing.setDate(event.getDate());
                    Event updated = repository.save(existing);
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
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
