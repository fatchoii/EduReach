package com.it342g02.vibecoder.edureach.dto;

import java.util.List;

public record SubmissionResultDto(
        Long submissionId,
        Long assessmentId,
        double score,
        boolean passed,
        int correctCount,
        int totalQuestions,
        List<AnswerResultDto> answers) {
}
