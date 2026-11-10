package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.LessonDto;
import com.it342g02.vibecoder.edureach.entity.Lesson;
import com.it342g02.vibecoder.edureach.entity.Module;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.LessonRepository;
import com.it342g02.vibecoder.edureach.repository.ModuleRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonService(LessonRepository lessonRepository, ModuleRepository moduleRepository) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }

    public List<LessonDto> findAll(Long moduleId) {
        List<Lesson> list;
        if (moduleId != null) {
            list = lessonRepository.findByModuleId(moduleId);
        } else {
            list = lessonRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public LessonDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public LessonDto create(LessonDto dto) {
        Lesson entity = new Lesson();
        apply(entity, dto);
        return toDto(lessonRepository.save(entity));
    }

    @Transactional
    public LessonDto update(Long id, LessonDto dto) {
        Lesson entity = get(id);
        apply(entity, dto);
        return toDto(lessonRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lesson not found with id " + id);
        }
        lessonRepository.deleteById(id);
    }

    private Lesson get(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id " + id));
    }

    private void apply(Lesson e, LessonDto d) {
        if (d.moduleId() != null) {
            e.setModule(moduleRepository.findById(d.moduleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + d.moduleId())));
        }
        if (d.title() != null) {
            e.setTitle(d.title());
        }
        if (d.description() != null) {
            e.setDescription(d.description());
        }
        if (d.instructions() != null) {
            e.setInstructions(d.instructions());
        }
        if (d.contentType() != null) {
            e.setContentType(d.contentType());
        }
        if (d.contentUrl() != null) {
            e.setContentUrl(d.contentUrl());
        }
        if (d.durationMinutes() != null) {
            e.setDurationMinutes(d.durationMinutes());
        }
        if (d.downloadable() != null) {
            e.setDownloadable(d.downloadable());
        }
        if (d.status() != null) {
            e.setStatus(d.status());
        }
    }

    private LessonDto toDto(Lesson e) {
        return new LessonDto(
                e.getId(),
                e.getModule().getId(),
                e.getTitle(),
                e.getDescription(),
                e.getInstructions(),
                e.getContentType(),
                e.getContentUrl(),
                e.getDurationMinutes(),
                e.getDownloadable(),
                e.getStatus(),
                e.getModule().getTitle());
    }
}
