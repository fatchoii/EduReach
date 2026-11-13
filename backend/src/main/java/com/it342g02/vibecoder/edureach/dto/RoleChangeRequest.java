package com.it342g02.vibecoder.edureach.dto;

import com.it342g02.vibecoder.edureach.entity.Role;
import jakarta.validation.constraints.NotNull;

public record RoleChangeRequest(@NotNull(message = "role is required") Role role) {
}
