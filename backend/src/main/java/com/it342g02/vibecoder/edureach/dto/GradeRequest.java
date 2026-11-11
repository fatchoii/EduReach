package com.it342g02.vibecoder.edureach.dto;

import java.util.List;

/** Instructor review: optional per-answer corrections, an optional final score, and feedback. */
public record GradeRequest(Double score, String feedback, List<AnswerOverride> overrides) {
}
