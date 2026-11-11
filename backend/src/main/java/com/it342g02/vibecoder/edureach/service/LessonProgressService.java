package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.LessonProgressDto;
import com.it342g02.vibecoder.edureach.entity.Lesson;
import com.it342g02.vibecoder.edureach.entity.LessonProgress;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.LessonProgressRepository;
import com.it342g02.vibecoder.edureach.repository.LessonRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LessonProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public LessonProgressService(LessonProgressRepository lessonProgressRepository, UserRepository userRepository, LessonRepository lessonRepository) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
    }

    public List<LessonProgressDto> findAll(Long userId, Long lessonId) {
        List<LessonProgress> list;
        if (userId != null && lessonId != null) {
            list = lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId);
        } else if (userId != null) {
            list = lessonProgressRepository.findByUserId(userId);
        } else if (lessonId != null) {
            list = lessonProgressRepository.findByLessonId(lessonId);
        } else {
            list = lessonProgressRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public LessonProgressDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public LessonProgressDto create(LessonProgressDto dto) {
        LessonProgress entity = new LessonProgress();
        apply(entity, dto);
        LessonProgress saved = lessonProgressRepository.save(entity);
        return toDto(saved);
    }

    @Transactional
    public LessonProgressDto update(Long id, LessonProgressDto dto) {
        LessonProgress entity = get(id);
        apply(entity, dto);
        return toDto(lessonProgressRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!lessonProgressRepository.existsById(id)) {
            throw new ResourceNotFoundException("LessonProgress not found with id " + id);
        }
        lessonProgressRepository.deleteById(id);
    }

    private LessonProgress get(Long id) {
        return lessonProgressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LessonProgress not found with id " + id));
    }

    private void apply(LessonProgress e, LessonProgressDto d) {
        if (d.userId() != null) {
            e.setUser(userRepository.findById(d.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + d.userId())));
        }
        if (d.lessonId() != null) {
            e.setLesson(lessonRepository.findById(d.lessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id " + d.lessonId())));
        }
        if (d.completed() != null) {
            e.setCompleted(d.completed());
        }
        if (d.completedAt() != null) {
            e.setCompletedAt(d.completedAt());
        }
    }

    private LessonProgressDto toDto(LessonProgress e) {
        return new LessonProgressDto(
                e.getId(),
                e.getUser().getId(),
                e.getLesson().getId(),
                e.getCompleted(),
                e.getCompletedAt(),
                e.getUser().getFullName(),
                e.getLesson().getTitle());
    }
}
