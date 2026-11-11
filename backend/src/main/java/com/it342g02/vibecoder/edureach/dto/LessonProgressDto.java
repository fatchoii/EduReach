package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record LessonProgressDto(
        Long id,
        @NotNull(message = "userId is required") Long userId,
        @NotNull(message = "lessonId is required") Long lessonId,
        Boolean completed,
        LocalDateTime completedAt,
        String userName,
        String lessonTitle) {
}
