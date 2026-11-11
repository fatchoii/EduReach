package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record SubmissionDto(
        Long id,
        @NotNull(message = "assessmentId is required") Long assessmentId,
        @NotNull(message = "userId is required") Long userId,
        Double score,
        LocalDateTime submittedAt,
        String status,
        String feedback,
        LocalDateTime gradedAt,
        Long gradedById,
        String userName,
        String assessmentTitle,
        String gradedByName) {
}
