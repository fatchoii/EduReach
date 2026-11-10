package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModuleDto(
        Long id,
        @NotNull(message = "courseId is required") Long courseId,
        @NotBlank(message = "title is required") String title,
        Integer orderIndex) {
}
