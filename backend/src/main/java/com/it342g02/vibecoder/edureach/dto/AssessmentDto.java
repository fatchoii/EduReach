package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AssessmentDto(
        Long id,
        @NotNull(message = "moduleId is required") Long moduleId,
        @NotBlank(message = "title is required") String title,
        String type,
        Double passingScore,
        LocalDate dueDate,
        String status,
        String moduleTitle) {
}
