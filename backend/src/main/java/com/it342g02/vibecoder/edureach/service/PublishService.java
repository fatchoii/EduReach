package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.AssessmentDto;
import com.it342g02.vibecoder.edureach.dto.CourseDto;
import com.it342g02.vibecoder.edureach.dto.LessonDto;
import com.it342g02.vibecoder.edureach.entity.Assessment;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.Lesson;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Publishing makes content visible to learners and alerts the learners enrolled in the course. */
@Service
public class PublishService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final AssessmentRepository assessmentRepository;
    private final AlertService alertService;
    private final CourseService courseService;
    private final LessonService lessonService;
    private final AssessmentService assessmentService;

    public PublishService(CourseRepository courseRepository, LessonRepository lessonRepository,
                          AssessmentRepository assessmentRepository, AlertService alertService,
                          CourseService courseService, LessonService lessonService,
                          AssessmentService assessmentService) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.assessmentRepository = assessmentRepository;
        this.alertService = alertService;
        this.courseService = courseService;
        this.lessonService = lessonService;
        this.assessmentService = assessmentService;
    }

    @Transactional
    public CourseDto setCoursePublished(Long id, boolean published) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + id));
        course.setStatus(published ? "PUBLISHED" : "DRAFT");
        courseRepository.save(course);
        if (published) {
            alertService.notifyCourseLearners(course, "NEW_CONTENT", "Course published: " + course.getTitle(),
                    course.getTitle() + " is now available.", "COURSE:" + id);
        }
        return courseService.findById(id);
    }

    @Transactional
    public LessonDto setLessonPublished(Long id, boolean published) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id " + id));
        lesson.setStatus(published ? "PUBLISHED" : "DRAFT");
        lessonRepository.save(lesson);
        if (published) {
            Course course = lesson.getModule().getCourse();
            alertService.notifyCourseLearners(course, "NEW_CONTENT", "New lesson: " + lesson.getTitle(),
                    "A new lesson was added to " + course.getTitle() + ".", "LESSON:" + id);
        }
        return lessonService.findById(id);
    }

    @Transactional
    public AssessmentDto setAssessmentPublished(Long id, boolean published) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id " + id));
        assessment.setStatus(published ? "ACTIVE" : "DRAFT");
        assessmentRepository.save(assessment);
        if (published) {
            Course course = assessment.getModule().getCourse();
            String due = assessment.getDueDate() == null ? "" : " Due " + assessment.getDueDate() + ".";
            alertService.notifyCourseLearners(course, "NEW_CONTENT", "New " + assessment.getType().toLowerCase() + ": "
                    + assessment.getTitle(), assessment.getTitle() + " is open in " + course.getTitle() + "." + due,
                    "ASSESSMENT:" + id);
        }
        return assessmentService.findById(id);
    }
}
