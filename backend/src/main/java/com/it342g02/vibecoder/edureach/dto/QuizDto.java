package com.it342g02.vibecoder.edureach.dto;

import java.time.LocalDate;
import java.util.List;

public record QuizDto(
        Long assessmentId,
        String title,
        String type,
        Double passingScore,
        LocalDate dueDate,
        List<QuizQuestionDto> questions) {
}
