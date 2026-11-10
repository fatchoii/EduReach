package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        @NotNull(message = "userId is required") Long userId,
        @NotBlank(message = "title is required") String title,
        String message,
        Boolean read,
        LocalDateTime createdAt) {
}
