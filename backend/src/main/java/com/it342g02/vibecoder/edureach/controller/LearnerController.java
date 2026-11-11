package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.CourseProgressDto;
import com.it342g02.vibecoder.edureach.dto.EnrollmentDto;
import com.it342g02.vibecoder.edureach.dto.NotificationDto;
import com.it342g02.vibecoder.edureach.dto.NotificationSettingDto;
import com.it342g02.vibecoder.edureach.dto.SubmissionDto;
import com.it342g02.vibecoder.edureach.service.LearnerService;
import com.it342g02.vibecoder.edureach.service.ProgressService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints for the signed-in user's own data. */
@RestController
@RequestMapping("/api")
public class LearnerController {

    private final LearnerService learnerService;
    private final ProgressService progressService;

    public LearnerController(LearnerService learnerService, ProgressService progressService) {
        this.learnerService = learnerService;
        this.progressService = progressService;
    }

    @GetMapping("/me/progress")
    public List<CourseProgressDto> myProgress(Authentication authentication) {
        return progressService.myProgress(authentication.getName());
    }

    @GetMapping("/me/completed-lessons")
    public List<Long> completedLessons(@RequestParam(required = false) Long courseId, Authentication authentication) {
        return progressService.completedLessonIds(authentication.getName(), courseId);
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public CourseProgressDto completeLesson(@PathVariable Long lessonId, Authentication authentication) {
        return progressService.completeLesson(lessonId, authentication.getName());
    }

    @GetMapping("/me/submissions")
    public List<SubmissionDto> mySubmissions(Authentication authentication) {
        return learnerService.mySubmissions(authentication.getName());
    }

    @PostMapping("/me/enroll/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentDto enroll(@PathVariable Long courseId, Authentication authentication) {
        return learnerService.enroll(authentication.getName(), courseId);
    }

    @GetMapping("/me/notifications")
    public List<NotificationDto> myNotifications(Authentication authentication) {
        return learnerService.myNotifications(authentication.getName());
    }

    @PostMapping("/me/notifications/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable Long id, Authentication authentication) {
        learnerService.markRead(authentication.getName(), id);
    }

    @PostMapping("/me/notifications/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(Authentication authentication) {
        learnerService.markAllRead(authentication.getName());
    }

    @GetMapping("/me/notification-settings")
    public NotificationSettingDto getSettings(Authentication authentication) {
        return learnerService.getSettings(authentication.getName());
    }

    @PutMapping("/me/notification-settings")
    public NotificationSettingDto updateSettings(@RequestBody NotificationSettingDto dto, Authentication authentication) {
        return learnerService.updateSettings(authentication.getName(), dto);
    }
}
