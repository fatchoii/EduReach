package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.ParentStudentDto;
import com.it342g02.vibecoder.edureach.entity.ParentStudent;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.ParentStudentRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ParentStudentService {

    private final ParentStudentRepository parentStudentRepository;
    private final UserRepository userRepository;

    public ParentStudentService(ParentStudentRepository parentStudentRepository, UserRepository userRepository) {
        this.parentStudentRepository = parentStudentRepository;
        this.userRepository = userRepository;
    }

    public List<ParentStudentDto> findAll(Long parentId, Long studentId) {
        List<ParentStudent> list;
        if (parentId != null && studentId != null) {
            list = parentStudentRepository.findByParentIdAndStudentId(parentId, studentId);
        } else if (parentId != null) {
            list = parentStudentRepository.findByParentId(parentId);
        } else if (studentId != null) {
            list = parentStudentRepository.findByStudentId(studentId);
        } else {
            list = parentStudentRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public ParentStudentDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public ParentStudentDto create(ParentStudentDto dto) {
        ParentStudent entity = new ParentStudent();
        apply(entity, dto);
        return toDto(parentStudentRepository.save(entity));
    }

    @Transactional
    public ParentStudentDto update(Long id, ParentStudentDto dto) {
        ParentStudent entity = get(id);
        apply(entity, dto);
        return toDto(parentStudentRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!parentStudentRepository.existsById(id)) {
            throw new ResourceNotFoundException("ParentStudent not found with id " + id);
        }
        parentStudentRepository.deleteById(id);
    }

    private ParentStudent get(Long id) {
        return parentStudentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ParentStudent not found with id " + id));
    }

    private void apply(ParentStudent e, ParentStudentDto d) {
        if (d.parentId() != null) {
            e.setParent(userRepository.findById(d.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + d.parentId())));
        }
        if (d.studentId() != null) {
            e.setStudent(userRepository.findById(d.studentId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + d.studentId())));
        }
    }

    private ParentStudentDto toDto(ParentStudent e) {
        return new ParentStudentDto(
                e.getId(),
                e.getParent().getId(),
                e.getStudent().getId(),
                e.getParent().getFullName(),
                e.getStudent().getFullName());
    }
}
