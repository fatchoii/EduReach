package com.it342g02.vibecoder.edureach.dto;

public record CourseProgressDto(
        Long courseId,
        String courseTitle,
        double progressPercent,
        int lessonsCompleted,
        int lessonsTotal,
        int assessmentsDone,
        int assessmentsTotal,
        String status) {
}
