package com.eventmaster.service;

import com.eventmaster.dto.RegistrationRequestDTO;
import com.eventmaster.dto.RegistrationResponseDTO;
import com.eventmaster.model.Event;
import com.eventmaster.model.Registration;
import com.eventmaster.model.User;
import com.eventmaster.repository.EventRepository;
import com.eventmaster.repository.RegistrationRepository;
import com.eventmaster.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RegistrationService(RegistrationRepository registrationRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponseDTO> findAll() {
        return registrationRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistrationResponseDTO findById(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found with id: " + id));
        return toResponseDTO(registration);
    }

    @Transactional
    public RegistrationResponseDTO create(@Valid RegistrationRequestDTO dto) {
        Event event = eventRepository.findById(dto.eventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Registration registration = new Registration();
        registration.setEvent(event);
        registration.setUser(user);

        Registration saved = registrationRepository.save(registration);
        return toResponseDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!registrationRepository.existsById(id)) {
            throw new EntityNotFoundException("Registration not found with id: " + id);
        }
        registrationRepository.deleteById(id);
    }

    private RegistrationResponseDTO toResponseDTO(Registration registration) {
        return new RegistrationResponseDTO(
                registration.getId(),
                registration.getEvent().getTitle(),
                registration.getUser().getName(),
                registration.getUser().getEmail(),
                registration.getRegisteredAt()
        );
    }
}