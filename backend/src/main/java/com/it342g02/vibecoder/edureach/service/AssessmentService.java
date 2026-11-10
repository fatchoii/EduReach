package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.AssessmentDto;
import com.it342g02.vibecoder.edureach.entity.Assessment;
import com.it342g02.vibecoder.edureach.entity.Module;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.ModuleRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final ModuleRepository moduleRepository;

    public AssessmentService(AssessmentRepository assessmentRepository, ModuleRepository moduleRepository) {
        this.assessmentRepository = assessmentRepository;
        this.moduleRepository = moduleRepository;
    }

    public List<AssessmentDto> findAll(Long moduleId) {
        List<Assessment> list;
        if (moduleId != null) {
            list = assessmentRepository.findByModuleId(moduleId);
        } else {
            list = assessmentRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public AssessmentDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public AssessmentDto create(AssessmentDto dto) {
        Assessment entity = new Assessment();
        apply(entity, dto);
        return toDto(assessmentRepository.save(entity));
    }

    @Transactional
    public AssessmentDto update(Long id, AssessmentDto dto) {
        Assessment entity = get(id);
        apply(entity, dto);
        return toDto(assessmentRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!assessmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assessment not found with id " + id);
        }
        assessmentRepository.deleteById(id);
    }

    private Assessment get(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id " + id));
    }

    private void apply(Assessment e, AssessmentDto d) {
        if (d.moduleId() != null) {
            e.setModule(moduleRepository.findById(d.moduleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + d.moduleId())));
        }
        if (d.title() != null) {
            e.setTitle(d.title());
        }
        if (d.type() != null) {
            e.setType(d.type());
        }
        if (d.passingScore() != null) {
            e.setPassingScore(d.passingScore());
        }
        if (d.dueDate() != null) {
            e.setDueDate(d.dueDate());
        }
        if (d.status() != null) {
            e.setStatus(d.status());
        }
    }

    private AssessmentDto toDto(Assessment e) {
        return new AssessmentDto(
                e.getId(),
                e.getModule().getId(),
                e.getTitle(),
                e.getType(),
                e.getPassingScore(),
                e.getDueDate(),
                e.getStatus(),
                e.getModule().getTitle());
    }
}
