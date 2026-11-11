package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record SyncLogDto(
        Long id,
        @NotNull(message = "userId is required") Long userId,
        String deviceId,
        String eventType,
        String clientId,
        String status,
        String message,
        LocalDateTime occurredAt,
        LocalDateTime syncedAt,
        String userName) {
}
