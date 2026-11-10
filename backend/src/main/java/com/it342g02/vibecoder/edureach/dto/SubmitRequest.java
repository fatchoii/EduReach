package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SubmitRequest(@NotNull(message = "answers is required") List<AnswerItem> answers) {
}
