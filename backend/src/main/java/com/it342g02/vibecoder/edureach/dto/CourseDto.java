package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CourseDto(
        Long id,
        @NotNull(message = "instructorId is required") Long instructorId,
        @NotBlank(message = "title is required") String title,
        String description,
        String category,
        String language,
        String status,
        LocalDateTime createdAt,
        String instructorName) {
}
