package com.it342g02.vibecoder.edureach.dto;

public record OfflineLessonDto(
        Long id,
        String title,
        String description,
        String contentType,
        String contentUrl,
        String textContent,
        String instructions,
        Integer durationMinutes,
        Boolean downloadable) {
}
