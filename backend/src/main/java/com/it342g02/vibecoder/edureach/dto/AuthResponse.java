package com.it342g02.vibecoder.edureach.dto;

import com.it342g02.vibecoder.edureach.entity.Role;

public record AuthResponse(String token, Long userId, String fullName, String email, Role role) {
}
