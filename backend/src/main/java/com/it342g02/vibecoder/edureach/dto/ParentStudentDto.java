package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParentStudentDto(
        Long id,
        @NotNull(message = "parentId is required") Long parentId,
        @NotNull(message = "studentId is required") Long studentId,
        String parentName,
        String studentName) {
}
