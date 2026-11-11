package com.it342g02.vibecoder.edureach.dto;

import java.time.LocalDateTime;
import java.util.List;

/** One thing that happened while offline. type is LESSON_COMPLETE or QUIZ_SUBMIT. */
public record SyncEvent(
        String clientId,
        String type,
        LocalDateTime occurredAt,
        Long lessonId,
        Long assessmentId,
        List<AnswerItem> answers) {
}
