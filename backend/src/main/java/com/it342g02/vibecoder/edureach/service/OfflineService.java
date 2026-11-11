package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.OfflineAssessmentDto;
import com.it342g02.vibecoder.edureach.dto.OfflineLessonDto;
import com.it342g02.vibecoder.edureach.dto.OfflineModuleDto;
import com.it342g02.vibecoder.edureach.dto.OfflinePackageDto;
import com.it342g02.vibecoder.edureach.dto.OfflineQuestionDto;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.Module;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.LessonRepository;
import com.it342g02.vibecoder.edureach.repository.ModuleRepository;
import com.it342g02.vibecoder.edureach.repository.QuestionBankRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Builds the downloadable offline bundle for a course: published lessons and active quizzes, no answers. */
@Service
@Transactional(readOnly = true)
public class OfflineService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final AssessmentRepository assessmentRepository;
    private final QuestionBankRepository questionBankRepository;
    private final SyncPolicyService syncPolicyService;
    private final ObjectMapper objectMapper;

    public OfflineService(CourseRepository courseRepository, ModuleRepository moduleRepository,
                          LessonRepository lessonRepository, AssessmentRepository assessmentRepository,
                          QuestionBankRepository questionBankRepository, SyncPolicyService syncPolicyService,
                          ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.assessmentRepository = assessmentRepository;
        this.questionBankRepository = questionBankRepository;
        this.syncPolicyService = syncPolicyService;
        this.objectMapper = objectMapper;
    }

    public OfflinePackageDto buildPackage(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + courseId));
        if (!"PUBLISHED".equals(course.getStatus())) {
            throw new ResourceNotFoundException("Course is not published");
        }
        int maxMb = syncPolicyService.current().getMaxPackageMb();

        List<OfflineModuleDto> modules = new ArrayList<>();
        for (Module module : moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId)) {
            List<OfflineLessonDto> lessons = lessonRepository.findByModuleIdAndStatus(module.getId(), "PUBLISHED").stream()
                    .filter(l -> !Boolean.FALSE.equals(l.getDownloadable()))
                    .map(l -> new OfflineLessonDto(l.getId(), l.getTitle(), l.getDescription(), l.getContentType(),
                            l.getContentUrl(), l.getTextContent(), l.getInstructions(), l.getDurationMinutes(),
                            l.getDownloadable()))
                    .toList();
            List<OfflineAssessmentDto> assessments = assessmentRepository.findByModuleIdAndStatus(module.getId(), "ACTIVE").stream()
                    .map(a -> new OfflineAssessmentDto(a.getId(), a.getTitle(), a.getType(), a.getPassingScore(),
                            a.getDueDate(),
                            questionBankRepository.findByAssessmentId(a.getId()).stream()
                                    .map(q -> new OfflineQuestionDto(q.getId(), q.getQuestionText(), q.getQuestionType(),
                                            splitOptions(q.getOptions()), q.getPoints()))
                                    .toList()))
                    .toList();
            modules.add(new OfflineModuleDto(module.getId(), module.getTitle(), module.getOrderIndex(), lessons, assessments));
        }

        OfflinePackageDto draft = new OfflinePackageDto(courseId, course.getTitle(), LocalDateTime.now(), 0L, true, maxMb, modules);
        long size;
        try {
            size = objectMapper.writeValueAsBytes(draft).length;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not build the offline package", ex);
        }
        boolean within = size <= maxMb * 1024L * 1024L;
        return new OfflinePackageDto(courseId, course.getTitle(), draft.generatedAt(), size, within, maxMb, modules);
    }

    private List<String> splitOptions(String options) {
        if (options == null || options.isBlank()) {
            return List.of();
        }
        return Arrays.stream(options.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
