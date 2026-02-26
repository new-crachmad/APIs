package com.eventmaster.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventRequestDTO(
        @NotBlank(message = "Title cannot be blank")
        String title,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotNull(message = "Start date is required")
        @Future(message = "Event start date must be in the future")
        LocalDateTime startAt,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long categoryId,

        @NotNull(message = "Organizer ID is required")
        @Positive(message = "Organizer ID must be positive")
        Long organizerId
) {}