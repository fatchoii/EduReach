package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.SubmissionResultDto;
import com.it342g02.vibecoder.edureach.dto.SubmitRequest;
import com.it342g02.vibecoder.edureach.service.GradingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assessments")
public class GradingController {

    private final GradingService gradingService;

    public GradingController(GradingService gradingService) {
        this.gradingService = gradingService;
    }

    /** The logged-in student submits answers; the server grades them and stores the result. */
    @PostMapping("/{assessmentId}/submit")
    public SubmissionResultDto submit(@PathVariable Long assessmentId, @Valid @RequestBody SubmitRequest request,
                                      Authentication authentication) {
        return gradingService.submit(assessmentId, authentication.getName(), request);
    }
}
