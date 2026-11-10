package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EnrollmentDto(
        Long id,
        @NotNull(message = "userId is required") Long userId,
        @NotNull(message = "courseId is required") Long courseId,
        LocalDate enrolledDate,
        String status,
        Double progressPercent,
        String userName,
        String courseTitle) {
}
