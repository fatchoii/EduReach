package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmissionAnswerDto(
        Long id,
        @NotNull(message = "submissionId is required") Long submissionId,
        @NotNull(message = "questionId is required") Long questionId,
        String studentAnswer,
        Boolean correct,
        String questionText,
        String correctAnswer) {
}
