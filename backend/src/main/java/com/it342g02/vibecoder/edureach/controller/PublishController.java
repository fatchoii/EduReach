package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.AssessmentDto;
import com.it342g02.vibecoder.edureach.dto.CourseDto;
import com.it342g02.vibecoder.edureach.dto.LessonDto;
import com.it342g02.vibecoder.edureach.service.PublishService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PublishController {

    private final PublishService publishService;

    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

    @PostMapping("/courses/{id}/publish")
    public CourseDto publishCourse(@PathVariable Long id) {
        return publishService.setCoursePublished(id, true);
    }

    @PostMapping("/courses/{id}/unpublish")
    public CourseDto unpublishCourse(@PathVariable Long id) {
        return publishService.setCoursePublished(id, false);
    }

    @PostMapping("/lessons/{id}/publish")
    public LessonDto publishLesson(@PathVariable Long id) {
        return publishService.setLessonPublished(id, true);
    }

    @PostMapping("/lessons/{id}/unpublish")
    public LessonDto unpublishLesson(@PathVariable Long id) {
        return publishService.setLessonPublished(id, false);
    }

    @PostMapping("/assessments/{id}/publish")
    public AssessmentDto publishAssessment(@PathVariable Long id) {
        return publishService.setAssessmentPublished(id, true);
    }

    @PostMapping("/assessments/{id}/unpublish")
    public AssessmentDto unpublishAssessment(@PathVariable Long id) {
        return publishService.setAssessmentPublished(id, false);
    }
}
