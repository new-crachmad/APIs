package com.eventmaster.dto;

import java.time.LocalDateTime;

public record RegistrationResponseDTO(
        Long id,
        String eventTitle,
        String userName,
        String userEmail,
        LocalDateTime registeredAt
) {}