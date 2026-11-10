package com.it342g02.vibecoder.edureach.dto;

public record AnswerResultDto(
        Long questionId,
        String questionText,
        String studentAnswer,
        String correctAnswer,
        boolean correct) {
}
