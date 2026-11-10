package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByInstructorId(Long instructorId);

}
