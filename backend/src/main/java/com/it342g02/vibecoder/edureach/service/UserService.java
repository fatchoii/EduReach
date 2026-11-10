package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.UserDto;
import com.it342g02.vibecoder.edureach.entity.Role;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAll(Role role) {
        List<User> users = role != null ? userRepository.findByRole(role) : userRepository.findAll();
        return users.stream().map(this::toDto).toList();
    }

    public UserDto findById(Long id) {
        return toDto(get(id));
    }

    public UserDto findByEmail(String email) {
        return toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    /** Teachers can edit anyone; everyone else can only edit their own profile. */
    @Transactional
    public UserDto update(Long id, UserDto dto, Authentication authentication) {
        User user = get(id);
        boolean teacher = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        boolean self = authentication.getName().equals(user.getEmail());
        if (!teacher && !self) {
            throw new AccessDeniedException("You can only edit your own profile");
        }
        user.setFullName(dto.fullName());
        if (dto.profilePhotoUrl() != null) {
            user.setProfilePhotoUrl(dto.profilePhotoUrl());
        }
        return toDto(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    private User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.getProfilePhotoUrl(), u.getCreatedAt());
    }
}
