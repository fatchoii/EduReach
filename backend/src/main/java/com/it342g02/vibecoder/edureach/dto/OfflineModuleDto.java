package com.it342g02.vibecoder.edureach.dto;

import java.util.List;

public record OfflineModuleDto(
        Long id,
        String title,
        Integer orderIndex,
        List<OfflineLessonDto> lessons,
        List<OfflineAssessmentDto> assessments) {
}
