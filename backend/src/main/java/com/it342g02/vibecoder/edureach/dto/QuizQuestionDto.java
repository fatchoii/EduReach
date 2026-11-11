package com.it342g02.vibecoder.edureach.dto;

import java.util.List;

/** A question as the learner sees it: never includes the correct answer. */
public record QuizQuestionDto(Long id, String questionText, String questionType, List<String> options, Integer points) {
}
