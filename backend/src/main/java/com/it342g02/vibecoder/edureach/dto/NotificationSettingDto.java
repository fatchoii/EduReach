package com.it342g02.vibecoder.edureach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationSettingDto(
        Long id,
        @NotNull(message = "userId is required") Long userId,
        Boolean deadlineReminders,
        Boolean newContentAlerts,
        Boolean liveSessionInvites,
        Boolean emailAlerts,
        Integer reminderDaysBefore) {
}
