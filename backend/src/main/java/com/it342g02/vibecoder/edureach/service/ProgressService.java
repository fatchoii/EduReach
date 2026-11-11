package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.CourseProgressDto;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.Enrollment;
import com.it342g02.vibecoder.edureach.entity.Lesson;
import com.it342g02.vibecoder.edureach.entity.LessonProgress;
import com.it342g02.vibecoder.edureach.entity.Module;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.EnrollmentRepository;
import com.it342g02.vibecoder.edureach.repository.LessonProgressRepository;
import com.it342g02.vibecoder.edureach.repository.LessonRepository;
import com.it342g02.vibecoder.edureach.repository.ModuleRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Progress = (completed lessons + attempted assessments) / (published lessons + active assessments).
 * It is recalculated immediately after every lesson completion or quiz submission.
 */
@Service
@Transactional(readOnly = true)
public class ProgressService {

    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final ModuleRepository moduleRepository;
    private final AssessmentRepository assessmentRepository;
    private final SubmissionRepository submissionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public ProgressService(LessonRepository lessonRepository, LessonProgressRepository lessonProgressRepository,
                           ModuleRepository moduleRepository, AssessmentRepository assessmentRepository,
                           SubmissionRepository submissionRepository, EnrollmentRepository enrollmentRepository,
                           UserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.moduleRepository = moduleRepository;
        this.assessmentRepository = assessmentRepository;
        this.submissionRepository = submissionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CourseProgressDto completeLesson(Long lessonId, String email) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id " + lessonId));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        markComplete(user, lesson, null);
        return recalculate(user, lesson.getModule().getCourse());
    }

    @Transactional
    public void markComplete(User user, Lesson lesson, LocalDateTime at) {
        if (!lessonProgressRepository.findByUserIdAndLessonId(user.getId(), lesson.getId()).isEmpty()) {
            return;
        }
        LessonProgress progress = new LessonProgress();
        progress.setUser(user);
        progress.setLesson(lesson);
        progress.setCompleted(true);
        if (at != null) {
            progress.setCompletedAt(at);
        }
        lessonProgressRepository.save(progress);
    }

    /** Recomputes the learner's progress in a course and stores it on their enrollment. */
    @Transactional
    public CourseProgressDto recalculate(User user, Course course) {
        CourseProgressDto result = compute(user, course);
        for (Enrollment enrollment : enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId())) {
            enrollment.setProgressPercent(result.progressPercent());
            if (result.progressPercent() >= 100.0) {
                enrollment.setStatus("COMPLETED");
            } else if (!"DROPPED".equals(enrollment.getStatus())) {
                enrollment.setStatus("ACTIVE");
            }
            enrollmentRepository.save(enrollment);
        }
        return result;
    }

    public List<CourseProgressDto> myProgress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return enrollmentRepository.findByUserId(user.getId()).stream()
                .map(enrollment -> compute(user, enrollment.getCourse()))
                .toList();
    }

    public List<Long> completedLessonIds(String email, Long courseId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return lessonProgressRepository.findByUserId(user.getId()).stream()
                .filter(p -> Boolean.TRUE.equals(p.getCompleted()))
                .filter(p -> courseId == null || p.getLesson().getModule().getCourse().getId().equals(courseId))
                .map(p -> p.getLesson().getId())
                .toList();
    }

    private CourseProgressDto compute(User user, Course course) {
        Set<Long> lessonIds = new HashSet<>();
        Set<Long> assessmentIds = new HashSet<>();
        for (Module module : moduleRepository.findByCourseIdOrderByOrderIndexAsc(course.getId())) {
            lessonRepository.findByModuleIdAndStatus(module.getId(), "PUBLISHED")
                    .forEach(lesson -> lessonIds.add(lesson.getId()));
            assessmentRepository.findByModuleIdAndStatus(module.getId(), "ACTIVE")
                    .forEach(assessment -> assessmentIds.add(assessment.getId()));
        }
        long lessonsDone = lessonProgressRepository.findByUserId(user.getId()).stream()
                .filter(p -> Boolean.TRUE.equals(p.getCompleted()))
                .filter(p -> lessonIds.contains(p.getLesson().getId()))
                .count();
        long assessmentsDone = submissionRepository.findByUserId(user.getId()).stream()
                .map(s -> s.getAssessment().getId())
                .filter(assessmentIds::contains)
                .distinct()
                .count();
        int total = lessonIds.size() + assessmentIds.size();
        double percent = total == 0 ? 0.0 : Math.round((lessonsDone + assessmentsDone) * 1000.0 / total) / 10.0;
        percent = Math.min(percent, 100.0);
        String status = percent >= 100.0 ? "COMPLETED" : "ACTIVE";
        return new CourseProgressDto(course.getId(), course.getTitle(), percent, (int) lessonsDone, lessonIds.size(),
                (int) assessmentsDone, assessmentIds.size(), status);
    }
}
