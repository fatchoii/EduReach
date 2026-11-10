package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.SubmissionDto;
import com.it342g02.vibecoder.edureach.entity.Assessment;
import com.it342g02.vibecoder.edureach.entity.Submission;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssessmentRepository assessmentRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository, AssessmentRepository assessmentRepository, UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.assessmentRepository = assessmentRepository;
        this.userRepository = userRepository;
    }

    public List<SubmissionDto> findAll(Long assessmentId, Long userId) {
        List<Submission> list;
        if (assessmentId != null && userId != null) {
            list = submissionRepository.findByAssessmentIdAndUserId(assessmentId, userId);
        } else if (assessmentId != null) {
            list = submissionRepository.findByAssessmentId(assessmentId);
        } else if (userId != null) {
            list = submissionRepository.findByUserId(userId);
        } else {
            list = submissionRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public SubmissionDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public SubmissionDto create(SubmissionDto dto) {
        Submission entity = new Submission();
        apply(entity, dto);
        return toDto(submissionRepository.save(entity));
    }

    @Transactional
    public SubmissionDto update(Long id, SubmissionDto dto) {
        Submission entity = get(id);
        apply(entity, dto);
        return toDto(submissionRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Submission not found with id " + id);
        }
        submissionRepository.deleteById(id);
    }

    private Submission get(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id " + id));
    }

    private void apply(Submission e, SubmissionDto d) {
        if (d.assessmentId() != null) {
            e.setAssessment(assessmentRepository.findById(d.assessmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id " + d.assessmentId())));
        }
        if (d.userId() != null) {
            e.setUser(userRepository.findById(d.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + d.userId())));
        }
        if (d.score() != null) {
            e.setScore(d.score());
        }
        if (d.submittedAt() != null) {
            e.setSubmittedAt(d.submittedAt());
        }
    }

    private SubmissionDto toDto(Submission e) {
        return new SubmissionDto(
                e.getId(),
                e.getAssessment().getId(),
                e.getUser().getId(),
                e.getScore(),
                e.getSubmittedAt(),
                e.getUser().getFullName(),
                e.getAssessment().getTitle());
    }
}
