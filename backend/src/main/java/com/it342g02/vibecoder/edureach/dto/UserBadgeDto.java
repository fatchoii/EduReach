package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UserBadgeDto(
        Long id,
        @NotNull(message = "userId is required") Long userId,
        @NotNull(message = "courseId is required") Long courseId,
        LocalDate issuedDate,
        String credentialUrl,
        String courseTitle) {
}
