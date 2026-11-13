package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.CourseDto;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public List<CourseDto> findAll(Long instructorId, String status) {
        List<Course> list;
        if (instructorId != null && status != null) {
            list = courseRepository.findByInstructorIdAndStatus(instructorId, status);
        } else if (instructorId != null) {
            list = courseRepository.findByInstructorId(instructorId);
        } else if (status != null) {
            list = courseRepository.findByStatus(status);
        } else {
            list = courseRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public CourseDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public CourseDto create(CourseDto dto) {
        Course entity = new Course();
        apply(entity, dto);
        Course saved = courseRepository.save(entity);
        return toDto(saved);
    }

    @Transactional
    public CourseDto update(Long id, CourseDto dto) {
        Course entity = get(id);
        apply(entity, dto);
        return toDto(courseRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id " + id);
        }
        courseRepository.deleteById(id);
    }

    private Course get(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + id));
    }

    private void apply(Course e, CourseDto d) {
        if (d.instructorId() != null) {
            e.setInstructor(userRepository.findById(d.instructorId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + d.instructorId())));
        }
        if (d.title() != null) {
            e.setTitle(d.title());
        }
        if (d.description() != null) {
            e.setDescription(d.description());
        }
        if (d.category() != null) {
            e.setCategory(d.category());
        }
        if (d.language() != null) {
            e.setLanguage(d.language());
        }
        if (d.status() != null) {
            e.setStatus(d.status());
        }
    }

    private CourseDto toDto(Course e) {
        return new CourseDto(
                e.getId(),
                e.getInstructor().getId(),
                e.getTitle(),
                e.getDescription(),
                e.getCategory(),
                e.getLanguage(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getInstructor().getFullName());
    }
}
