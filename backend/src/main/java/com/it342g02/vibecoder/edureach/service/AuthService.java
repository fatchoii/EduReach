package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.AuthResponse;
import com.it342g02.vibecoder.edureach.dto.LoginRequest;
import com.it342g02.vibecoder.edureach.dto.RegisterRequest;
import com.it342g02.vibecoder.edureach.entity.Role;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.DuplicateResourceException;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import com.it342g02.vibecoder.edureach.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final String adminSignupCode;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       @Value("${app.admin.signup-code}") String adminSignupCode) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.adminSignupCode = adminSignupCode;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }
        if (request.role() == Role.ADMIN && !adminSignupCode.equals(request.adminCode())) {
            throw new AccessDeniedException("Invalid admin code");
        }
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user = userRepository.save(user);
        return toResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
