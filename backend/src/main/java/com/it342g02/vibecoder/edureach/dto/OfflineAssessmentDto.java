package com.it342g02.vibecoder.edureach.dto;

import java.time.LocalDate;
import java.util.List;

public record OfflineAssessmentDto(
        Long id,
        String title,
        String type,
        Double passingScore,
        LocalDate dueDate,
        List<OfflineQuestionDto> questions) {
}
