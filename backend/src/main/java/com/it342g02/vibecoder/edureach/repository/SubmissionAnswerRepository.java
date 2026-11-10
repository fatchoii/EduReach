package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.SubmissionAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, Long> {

    List<SubmissionAnswer> findBySubmissionId(Long submissionId);

}
