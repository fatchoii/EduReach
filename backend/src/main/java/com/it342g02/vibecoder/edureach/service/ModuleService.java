package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.ModuleDto;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.Module;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.ModuleRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public ModuleService(ModuleRepository moduleRepository, CourseRepository courseRepository) {
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
    }

    public List<ModuleDto> findAll(Long courseId) {
        List<Module> list;
        if (courseId != null) {
            list = moduleRepository.findByCourseId(courseId);
        } else {
            list = moduleRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public ModuleDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public ModuleDto create(ModuleDto dto) {
        Module entity = new Module();
        apply(entity, dto);
        return toDto(moduleRepository.save(entity));
    }

    @Transactional
    public ModuleDto update(Long id, ModuleDto dto) {
        Module entity = get(id);
        apply(entity, dto);
        return toDto(moduleRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!moduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Module not found with id " + id);
        }
        moduleRepository.deleteById(id);
    }

    private Module get(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + id));
    }

    private void apply(Module e, ModuleDto d) {
        if (d.courseId() != null) {
            e.setCourse(courseRepository.findById(d.courseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + d.courseId())));
        }
        if (d.title() != null) {
            e.setTitle(d.title());
        }
        if (d.orderIndex() != null) {
            e.setOrderIndex(d.orderIndex());
        }
    }

    private ModuleDto toDto(Module e) {
        return new ModuleDto(
                e.getId(),
                e.getCourse().getId(),
                e.getTitle(),
                e.getOrderIndex());
    }
}
