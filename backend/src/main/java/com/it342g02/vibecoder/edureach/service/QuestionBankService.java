package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.QuestionBankDto;
import com.it342g02.vibecoder.edureach.entity.Assessment;
import com.it342g02.vibecoder.edureach.entity.QuestionBank;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.QuestionBankRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final AssessmentRepository assessmentRepository;

    public QuestionBankService(QuestionBankRepository questionBankRepository, AssessmentRepository assessmentRepository) {
        this.questionBankRepository = questionBankRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public List<QuestionBankDto> findAll(Long assessmentId, Long moduleId) {
        List<QuestionBank> list;
        if (assessmentId != null && moduleId != null) {
            list = questionBankRepository.findByAssessmentIdAndAssessmentModuleId(assessmentId, moduleId);
        } else if (assessmentId != null) {
            list = questionBankRepository.findByAssessmentId(assessmentId);
        } else if (moduleId != null) {
            list = questionBankRepository.findByAssessmentModuleId(moduleId);
        } else {
            list = questionBankRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public QuestionBankDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public QuestionBankDto create(QuestionBankDto dto) {
        QuestionBank entity = new QuestionBank();
        apply(entity, dto);
        return toDto(questionBankRepository.save(entity));
    }

    @Transactional
    public QuestionBankDto update(Long id, QuestionBankDto dto) {
        QuestionBank entity = get(id);
        apply(entity, dto);
        return toDto(questionBankRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!questionBankRepository.existsById(id)) {
            throw new ResourceNotFoundException("QuestionBank not found with id " + id);
        }
        questionBankRepository.deleteById(id);
    }

    private QuestionBank get(Long id) {
        return questionBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionBank not found with id " + id));
    }

    private void apply(QuestionBank e, QuestionBankDto d) {
        if (d.assessmentId() != null) {
            e.setAssessment(assessmentRepository.findById(d.assessmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id " + d.assessmentId())));
        }
        if (d.questionText() != null) {
            e.setQuestionText(d.questionText());
        }
        if (d.questionType() != null) {
            e.setQuestionType(d.questionType());
        }
        if (d.points() != null) {
            e.setPoints(d.points());
        }
        if (d.correctAnswer() != null) {
            e.setCorrectAnswer(d.correctAnswer());
        }
        if (d.difficulty() != null) {
            e.setDifficulty(d.difficulty());
        }
    }

    private QuestionBankDto toDto(QuestionBank e) {
        return new QuestionBankDto(
                e.getId(),
                e.getAssessment().getId(),
                e.getQuestionText(),
                e.getQuestionType(),
                e.getPoints(),
                e.getCorrectAnswer(),
                e.getDifficulty(),
                e.getAssessment().getTitle());
    }
}
