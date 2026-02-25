package com.eventmaster.dto;

import java.time.LocalDateTime;

public record EventRequestDTO(
        String title,
        String description,
        LocalDateTime startAt,
        Long categoryId,
        Long organizerId
) {
}
