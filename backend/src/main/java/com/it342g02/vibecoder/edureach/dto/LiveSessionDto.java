package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record LiveSessionDto(
        Long id,
        @NotNull(message = "courseId is required") Long courseId,
        @NotBlank(message = "title is required") String title,
        String description,
        LocalDateTime scheduledAt,
        String meetingUrl,
        String courseTitle) {
}
