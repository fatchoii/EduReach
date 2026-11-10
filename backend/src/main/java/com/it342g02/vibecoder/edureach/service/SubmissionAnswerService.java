package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.SubmissionAnswerDto;
import com.it342g02.vibecoder.edureach.entity.QuestionBank;
import com.it342g02.vibecoder.edureach.entity.Submission;
import com.it342g02.vibecoder.edureach.entity.SubmissionAnswer;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.QuestionBankRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionAnswerRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SubmissionAnswerService {

    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final SubmissionRepository submissionRepository;
    private final QuestionBankRepository questionBankRepository;

    public SubmissionAnswerService(SubmissionAnswerRepository submissionAnswerRepository, SubmissionRepository submissionRepository, QuestionBankRepository questionBankRepository) {
        this.submissionAnswerRepository = submissionAnswerRepository;
        this.submissionRepository = submissionRepository;
        this.questionBankRepository = questionBankRepository;
    }

    public List<SubmissionAnswerDto> findAll(Long submissionId) {
        List<SubmissionAnswer> list;
        if (submissionId != null) {
            list = submissionAnswerRepository.findBySubmissionId(submissionId);
        } else {
            list = submissionAnswerRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public SubmissionAnswerDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public SubmissionAnswerDto create(SubmissionAnswerDto dto) {
        SubmissionAnswer entity = new SubmissionAnswer();
        apply(entity, dto);
        return toDto(submissionAnswerRepository.save(entity));
    }

    @Transactional
    public SubmissionAnswerDto update(Long id, SubmissionAnswerDto dto) {
        SubmissionAnswer entity = get(id);
        apply(entity, dto);
        return toDto(submissionAnswerRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!submissionAnswerRepository.existsById(id)) {
            throw new ResourceNotFoundException("SubmissionAnswer not found with id " + id);
        }
        submissionAnswerRepository.deleteById(id);
    }

    private SubmissionAnswer get(Long id) {
        return submissionAnswerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubmissionAnswer not found with id " + id));
    }

    private void apply(SubmissionAnswer e, SubmissionAnswerDto d) {
        if (d.submissionId() != null) {
            e.setSubmission(submissionRepository.findById(d.submissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id " + d.submissionId())));
        }
        if (d.questionId() != null) {
            e.setQuestion(questionBankRepository.findById(d.questionId())
                    .orElseThrow(() -> new ResourceNotFoundException("QuestionBank not found with id " + d.questionId())));
        }
        if (d.studentAnswer() != null) {
            e.setStudentAnswer(d.studentAnswer());
        }
        if (d.correct() != null) {
            e.setCorrect(d.correct());
        }
    }

    private SubmissionAnswerDto toDto(SubmissionAnswer e) {
        return new SubmissionAnswerDto(
                e.getId(),
                e.getSubmission().getId(),
                e.getQuestion().getId(),
                e.getStudentAnswer(),
                e.getCorrect(),
                e.getQuestion().getQuestionText());
    }
}
