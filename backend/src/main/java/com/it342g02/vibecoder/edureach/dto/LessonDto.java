package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LessonDto(
        Long id,
        @NotNull(message = "moduleId is required") Long moduleId,
        @NotBlank(message = "title is required") String title,
        String description,
        String instructions,
        String textContent,
        String contentType,
        String contentUrl,
        Integer durationMinutes,
        Boolean downloadable,
        String status,
        String moduleTitle) {
}
