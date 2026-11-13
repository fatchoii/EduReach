package com.it342g02.vibecoder.edureach.dto;

public record SyncResultDto(String clientId, String status, String message, Long submissionId, Double score) {
}
