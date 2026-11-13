package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.LessonProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByUserId(Long userId);

    List<LessonProgress> findByLessonId(Long lessonId);

    List<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

}
