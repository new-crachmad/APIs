package com.eventmaster.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrationRequestDTO(
        @NotNull(message = "Event ID is required")
        @Positive(message = "Event ID must be positive")
        Long eventId,

        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be positive")
        Long userId
) {}