package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.Submission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByAssessmentId(Long assessmentId);

    List<Submission> findByUserId(Long userId);

    List<Submission> findByAssessmentIdAndUserId(Long assessmentId, Long userId);

    List<Submission> findByAssessmentModuleCourseId(Long courseId);

}
