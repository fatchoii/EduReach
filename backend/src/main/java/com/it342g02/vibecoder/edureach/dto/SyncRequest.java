package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SyncRequest(String deviceId, @NotNull(message = "events is required") List<SyncEvent> events) {
}
