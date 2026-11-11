package com.it342g02.vibecoder.edureach.dto;

import com.it342g02.vibecoder.edureach.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "fullName is required") String fullName,
        @NotBlank(message = "email is required") @Email(message = "email must be valid") String email,
        @NotBlank(message = "password is required") @Size(min = 6, message = "password must be at least 6 characters") String password,
        @NotNull(message = "role is required") Role role,
        String adminCode) {
}
