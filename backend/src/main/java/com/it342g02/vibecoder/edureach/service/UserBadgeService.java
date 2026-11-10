package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.UserBadgeDto;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.entity.UserBadge;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.UserBadgeRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserBadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserBadgeService(UserBadgeRepository userBadgeRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public List<UserBadgeDto> findAll(Long userId, Long courseId) {
        List<UserBadge> list;
        if (userId != null && courseId != null) {
            list = userBadgeRepository.findByUserIdAndCourseId(userId, courseId);
        } else if (userId != null) {
            list = userBadgeRepository.findByUserId(userId);
        } else if (courseId != null) {
            list = userBadgeRepository.findByCourseId(courseId);
        } else {
            list = userBadgeRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public UserBadgeDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public UserBadgeDto create(UserBadgeDto dto) {
        UserBadge entity = new UserBadge();
        apply(entity, dto);
        return toDto(userBadgeRepository.save(entity));
    }

    @Transactional
    public UserBadgeDto update(Long id, UserBadgeDto dto) {
        UserBadge entity = get(id);
        apply(entity, dto);
        return toDto(userBadgeRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!userBadgeRepository.existsById(id)) {
            throw new ResourceNotFoundException("UserBadge not found with id " + id);
        }
        userBadgeRepository.deleteById(id);
    }

    private UserBadge get(Long id) {
        return userBadgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserBadge not found with id " + id));
    }

    private void apply(UserBadge e, UserBadgeDto d) {
        if (d.userId() != null) {
            e.setUser(userRepository.findById(d.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + d.userId())));
        }
        if (d.courseId() != null) {
            e.setCourse(courseRepository.findById(d.courseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + d.courseId())));
        }
        if (d.issuedDate() != null) {
            e.setIssuedDate(d.issuedDate());
        }
        if (d.credentialUrl() != null) {
            e.setCredentialUrl(d.credentialUrl());
        }
    }

    private UserBadgeDto toDto(UserBadge e) {
        return new UserBadgeDto(
                e.getId(),
                e.getUser().getId(),
                e.getCourse().getId(),
                e.getIssuedDate(),
                e.getCredentialUrl(),
                e.getCourse().getTitle());
    }
}
