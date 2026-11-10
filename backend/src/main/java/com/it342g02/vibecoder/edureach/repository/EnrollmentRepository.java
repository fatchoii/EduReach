package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.Enrollment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByUserId(Long userId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

}
