package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.GradeRequest;
import com.it342g02.vibecoder.edureach.dto.QuizDto;
import com.it342g02.vibecoder.edureach.dto.SubmissionDto;
import com.it342g02.vibecoder.edureach.dto.SubmissionResultDto;
import com.it342g02.vibecoder.edureach.dto.SubmitRequest;
import com.it342g02.vibecoder.edureach.service.GradingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GradingController {

    private final GradingService gradingService;

    public GradingController(GradingService gradingService) {
        this.gradingService = gradingService;
    }

    /** The quiz without its correct answers, for the learner taking it. */
    @GetMapping("/assessments/{assessmentId}/take")
    public QuizDto take(@PathVariable Long assessmentId) {
        return gradingService.getQuiz(assessmentId);
    }

    /** The logged-in learner submits answers; the server grades them and stores the result. */
    @PostMapping("/assessments/{assessmentId}/submit")
    public SubmissionResultDto submit(@PathVariable Long assessmentId, @Valid @RequestBody SubmitRequest request,
                                      Authentication authentication) {
        return gradingService.submit(assessmentId, authentication.getName(), request);
    }

    /** Instructor review of a submission: corrections, score, feedback. */
    @PostMapping("/submissions/{submissionId}/grade")
    public SubmissionDto grade(@PathVariable Long submissionId, @RequestBody GradeRequest request,
                               Authentication authentication) {
        return gradingService.grade(submissionId, request, authentication.getName());
    }
}
