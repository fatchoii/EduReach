package com.it342g02.vibecoder.edureach.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Everything a learner needs to study a course without a connection (no correct answers included). */
public record OfflinePackageDto(
        Long courseId,
        String courseTitle,
        LocalDateTime generatedAt,
        long sizeBytes,
        boolean withinLimit,
        int maxPackageMb,
        List<OfflineModuleDto> modules) {
}
