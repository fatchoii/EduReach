package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.EnrollmentDto;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.Enrollment;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.EnrollmentRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public List<EnrollmentDto> findAll(Long userId, Long courseId) {
        List<Enrollment> list;
        if (userId != null && courseId != null) {
            list = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        } else if (userId != null) {
            list = enrollmentRepository.findByUserId(userId);
        } else if (courseId != null) {
            list = enrollmentRepository.findByCourseId(courseId);
        } else {
            list = enrollmentRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public EnrollmentDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public EnrollmentDto create(EnrollmentDto dto) {
        Enrollment entity = new Enrollment();
        apply(entity, dto);
        return toDto(enrollmentRepository.save(entity));
    }

    @Transactional
    public EnrollmentDto update(Long id, EnrollmentDto dto) {
        Enrollment entity = get(id);
        apply(entity, dto);
        return toDto(enrollmentRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enrollment not found with id " + id);
        }
        enrollmentRepository.deleteById(id);
    }

    private Enrollment get(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id " + id));
    }

    private void apply(Enrollment e, EnrollmentDto d) {
        if (d.userId() != null) {
            e.setUser(userRepository.findById(d.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + d.userId())));
        }
        if (d.courseId() != null) {
            e.setCourse(courseRepository.findById(d.courseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + d.courseId())));
        }
        if (d.enrolledDate() != null) {
            e.setEnrolledDate(d.enrolledDate());
        }
        if (d.status() != null) {
            e.setStatus(d.status());
        }
        if (d.progressPercent() != null) {
            e.setProgressPercent(d.progressPercent());
        }
    }

    private EnrollmentDto toDto(Enrollment e) {
        return new EnrollmentDto(
                e.getId(),
                e.getUser().getId(),
                e.getCourse().getId(),
                e.getEnrolledDate(),
                e.getStatus(),
                e.getProgressPercent(),
                e.getUser().getFullName(),
                e.getCourse().getTitle());
    }
}
