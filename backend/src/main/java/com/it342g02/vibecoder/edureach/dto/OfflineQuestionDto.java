package com.it342g02.vibecoder.edureach.dto;

import java.util.List;

public record OfflineQuestionDto(Long id, String questionText, String questionType, List<String> options, Integer points) {
}
