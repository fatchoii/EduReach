package com.it342g02.vibecoder.edureach.dto;

import com.it342g02.vibecoder.edureach.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record UserDto(
        Long id,
        @NotBlank(message = "fullName is required") String fullName,
        @NotBlank(message = "email is required") @Email(message = "email must be valid") String email,
        Role role,
        String profilePhotoUrl,
        LocalDateTime createdAt) {
}
