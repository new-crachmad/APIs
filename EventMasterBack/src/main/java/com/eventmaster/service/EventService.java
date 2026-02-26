package com.eventmaster.service;

import com.eventmaster.dto.EventRequestDTO;
import com.eventmaster.dto.EventResponseDTO;
import com.eventmaster.model.Category;
import com.eventmaster.model.Event;
import com.eventmaster.model.User;
import com.eventmaster.repository.CategoryRepository;
import com.eventmaster.repository.EventRepository;
import com.eventmaster.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<EventResponseDTO> findAll() {
        return eventRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponseDTO findById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        return toResponseDTO(event);
    }

    @Transactional
    public EventResponseDTO create(EventRequestDTO dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        User organizer = userRepository.findById(dto.organizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer (User) not found"));

        Event event = new Event();
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setStartAt(dto.startAt());
        event.setCategory(category);
        event.setOrganizer(organizer);

        Event savedEvent = eventRepository.save(event);
        return toResponseDTO(savedEvent);
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    // Método auxiliar para converter Entidade -> DTO
    private EventResponseDTO toResponseDTO(Event event) {
        // Tratamento para evitar NullPointerException se category/organizer forem nulos (embora não devam ser)
        String categoryName = event.getCategory() != null ? event.getCategory().getName() : null;
        String organizerName = event.getOrganizer() != null ? event.getOrganizer().getName() : null;

        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartAt(),
                categoryName,
                organizerName
        );
    }
}