package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.QuestionBank;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {

    List<QuestionBank> findByAssessmentId(Long assessmentId);

    List<QuestionBank> findByAssessmentModuleId(Long moduleId);

    List<QuestionBank> findByAssessmentIdAndAssessmentModuleId(Long assessmentId, Long moduleId);

}
