package com.eventmaster.dto;

import java.time.LocalDateTime;

public record EventResponseDTO(
        Long id,
        String title,
        String description,
        LocalDateTime startAt,
        String categoryName,
        String organizerName
) {}