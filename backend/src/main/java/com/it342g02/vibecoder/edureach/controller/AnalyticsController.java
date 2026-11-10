package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.StudentMasteryDto;
import com.it342g02.vibecoder.edureach.service.AnalyticsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/courses/{courseId}/mastery")
    public List<StudentMasteryDto> courseMastery(@PathVariable Long courseId) {
        return analyticsService.courseMastery(courseId);
    }

    @GetMapping("/students/{studentId}/mastery")
    public StudentMasteryDto studentMastery(@PathVariable Long studentId) {
        return analyticsService.studentMastery(studentId);
    }
}
