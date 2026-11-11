package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.LiveSessionDto;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.LiveSession;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.LiveSessionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LiveSessionService {

    private final LiveSessionRepository liveSessionRepository;
    private final CourseRepository courseRepository;
    private final AlertService alertService;

    public LiveSessionService(LiveSessionRepository liveSessionRepository, CourseRepository courseRepository, AlertService alertService) {
        this.liveSessionRepository = liveSessionRepository;
        this.courseRepository = courseRepository;
        this.alertService = alertService;
    }

    public List<LiveSessionDto> findAll(Long courseId) {
        List<LiveSession> list;
        if (courseId != null) {
            list = liveSessionRepository.findByCourseId(courseId);
        } else {
            list = liveSessionRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public LiveSessionDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public LiveSessionDto create(LiveSessionDto dto) {
        LiveSession entity = new LiveSession();
        apply(entity, dto);
        LiveSession saved = liveSessionRepository.save(entity);
        alertService.onLiveSessionScheduled(saved);
        return toDto(saved);
    }

    @Transactional
    public LiveSessionDto update(Long id, LiveSessionDto dto) {
        LiveSession entity = get(id);
        apply(entity, dto);
        return toDto(liveSessionRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!liveSessionRepository.existsById(id)) {
            throw new ResourceNotFoundException("LiveSession not found with id " + id);
        }
        liveSessionRepository.deleteById(id);
    }

    private LiveSession get(Long id) {
        return liveSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LiveSession not found with id " + id));
    }

    private void apply(LiveSession e, LiveSessionDto d) {
        if (d.courseId() != null) {
            e.setCourse(courseRepository.findById(d.courseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + d.courseId())));
        }
        if (d.title() != null) {
            e.setTitle(d.title());
        }
        if (d.description() != null) {
            e.setDescription(d.description());
        }
        if (d.scheduledAt() != null) {
            e.setScheduledAt(d.scheduledAt());
        }
        if (d.meetingUrl() != null) {
            e.setMeetingUrl(d.meetingUrl());
        }
    }

    private LiveSessionDto toDto(LiveSession e) {
        return new LiveSessionDto(
                e.getId(),
                e.getCourse().getId(),
                e.getTitle(),
                e.getDescription(),
                e.getScheduledAt(),
                e.getMeetingUrl(),
                e.getCourse().getTitle());
    }
}
