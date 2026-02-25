package com.eventmaster.dto;

import jakarta.validation.constraints.NotNull;

public record RegistrationRequestDTO(
        @NotNull(message = "Event ID is required")
        Long eventId,

        @NotNull(message = "User ID is required")
        Long userId
) {}