package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionBankDto(
        Long id,
        @NotNull(message = "assessmentId is required") Long assessmentId,
        @NotBlank(message = "questionText is required") String questionText,
        String questionType,
        Integer points,
        String correctAnswer,
        String difficulty,
        String assessmentTitle) {
}
