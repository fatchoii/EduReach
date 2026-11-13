package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.AnswerItem;
import com.it342g02.vibecoder.edureach.dto.AnswerOverride;
import com.it342g02.vibecoder.edureach.dto.AnswerResultDto;
import com.it342g02.vibecoder.edureach.dto.GradeRequest;
import com.it342g02.vibecoder.edureach.dto.QuizDto;
import com.it342g02.vibecoder.edureach.dto.QuizQuestionDto;
import com.it342g02.vibecoder.edureach.dto.SubmissionDto;
import com.it342g02.vibecoder.edureach.dto.SubmissionResultDto;
import com.it342g02.vibecoder.edureach.dto.SubmitRequest;
import com.it342g02.vibecoder.edureach.entity.Assessment;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.QuestionBank;
import com.it342g02.vibecoder.edureach.entity.Submission;
import com.it342g02.vibecoder.edureach.entity.SubmissionAnswer;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.QuestionBankRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionAnswerRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Quizzes are graded automatically. Assignments are auto-scored too but wait in the instructor's
 * review queue (PENDING_REVIEW) until an instructor grades them.
 */
@Service
public class GradingService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionBankRepository questionBankRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final UserRepository userRepository;
    private final ProgressService progressService;
    private final AlertService alertService;
    private final SubmissionService submissionService;

    public GradingService(AssessmentRepository assessmentRepository, QuestionBankRepository questionBankRepository,
                          SubmissionRepository submissionRepository, SubmissionAnswerRepository submissionAnswerRepository,
                          UserRepository userRepository, ProgressService progressService, AlertService alertService,
                          SubmissionService submissionService) {
        this.assessmentRepository = assessmentRepository;
        this.questionBankRepository = questionBankRepository;
        this.submissionRepository = submissionRepository;
        this.submissionAnswerRepository = submissionAnswerRepository;
        this.userRepository = userRepository;
        this.progressService = progressService;
        this.alertService = alertService;
        this.submissionService = submissionService;
    }

    /** The quiz as the learner should see it (no correct answers). */
    @Transactional(readOnly = true)
    public QuizDto getQuiz(Long assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id " + assessmentId));
        List<QuizQuestionDto> questions = questionBankRepository.findByAssessmentId(assessmentId).stream()
                .map(q -> new QuizQuestionDto(q.getId(), q.getQuestionText(), q.getQuestionType(),
                        splitOptions(q.getOptions()), q.getPoints()))
                .toList();
        return new QuizDto(assessment.getId(), assessment.getTitle(), assessment.getType(),
                assessment.getPassingScore(), assessment.getDueDate(), questions);
    }

    @Transactional
    public SubmissionResultDto submit(Long assessmentId, String email, SubmitRequest request) {
        return submit(assessmentId, email, request, null);
    }

    @Transactional
    public SubmissionResultDto submit(Long assessmentId, String email, SubmitRequest request, LocalDateTime submittedAt) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id " + assessmentId));
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<QuestionBank> questions = questionBankRepository.findByAssessmentId(assessmentId);

        Map<Long, String> given = new HashMap<>();
        for (AnswerItem item : request.answers()) {
            if (item.questionId() != null) {
                given.put(item.questionId(), item.answer());
            }
        }

        boolean needsReview = "ASSIGNMENT".equalsIgnoreCase(assessment.getType());
        Submission submission = new Submission();
        submission.setAssessment(assessment);
        submission.setUser(student);
        submission.setScore(0.0);
        submission.setStatus(needsReview ? "PENDING_REVIEW" : "GRADED");
        if (submittedAt != null) {
            submission.setSubmittedAt(submittedAt);
        }
        submission = submissionRepository.save(submission);

        int totalPoints = 0;
        int earnedPoints = 0;
        int correctCount = 0;
        List<SubmissionAnswer> answers = new ArrayList<>();
        List<AnswerResultDto> results = new ArrayList<>();
        for (QuestionBank question : questions) {
            int points = question.getPoints() == null ? 1 : question.getPoints();
            String studentAnswer = given.get(question.getId());
            boolean correct = matches(studentAnswer, question.getCorrectAnswer());
            totalPoints += points;
            if (correct) {
                earnedPoints += points;
                correctCount++;
            }
            SubmissionAnswer answer = new SubmissionAnswer();
            answer.setSubmission(submission);
            answer.setQuestion(question);
            answer.setStudentAnswer(studentAnswer);
            answer.setCorrect(correct);
            answers.add(answer);
            results.add(new AnswerResultDto(question.getId(), question.getQuestionText(), studentAnswer,
                    question.getCorrectAnswer(), correct));
        }
        submissionAnswerRepository.saveAll(answers);

        double score = totalPoints == 0 ? 0.0 : Math.round(earnedPoints * 1000.0 / totalPoints) / 10.0;
        submission.setScore(score);
        submissionRepository.save(submission);

        Course course = assessment.getModule().getCourse();
        progressService.recalculate(student, course);
        if (needsReview) {
            alertService.notifyUser(course.getInstructor(), "GENERAL", "New submission to review",
                    student.getFullName() + " submitted " + assessment.getTitle() + ".", null);
        }

        boolean passed = assessment.getPassingScore() == null || score >= assessment.getPassingScore();
        return new SubmissionResultDto(submission.getId(), assessmentId, score, passed, correctCount,
                questions.size(), submission.getStatus(), results);
    }

    /** Instructor review: correct individual answers and/or set the score, add feedback, mark as graded. */
    @Transactional
    public SubmissionDto grade(Long submissionId, GradeRequest request, String graderEmail) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id " + submissionId));
        User grader = userRepository.findByEmail(graderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.overrides() != null && !request.overrides().isEmpty()) {
            Map<Long, Boolean> corrections = new HashMap<>();
            for (AnswerOverride override : request.overrides()) {
                if (override.answerId() != null && override.correct() != null) {
                    corrections.put(override.answerId(), override.correct());
                }
            }
            List<SubmissionAnswer> answers = submissionAnswerRepository.findBySubmissionId(submissionId);
            int total = 0;
            int earned = 0;
            for (SubmissionAnswer answer : answers) {
                if (corrections.containsKey(answer.getId())) {
                    answer.setCorrect(corrections.get(answer.getId()));
                }
                int points = answer.getQuestion().getPoints() == null ? 1 : answer.getQuestion().getPoints();
                total += points;
                if (Boolean.TRUE.equals(answer.getCorrect())) {
                    earned += points;
                }
            }
            submissionAnswerRepository.saveAll(answers);
            submission.setScore(total == 0 ? 0.0 : Math.round(earned * 1000.0 / total) / 10.0);
        }
        if (request.score() != null) {
            submission.setScore(Math.max(0.0, Math.min(100.0, request.score())));
        }
        submission.setFeedback(request.feedback());
        submission.setStatus("GRADED");
        submission.setGradedAt(LocalDateTime.now());
        submission.setGradedBy(grader);
        submissionRepository.save(submission);

        String feedback = request.feedback() == null || request.feedback().isBlank() ? "" : " Feedback: " + request.feedback();
        alertService.notifyUser(submission.getUser(), "GRADE", "Your submission was graded",
                submission.getAssessment().getTitle() + ": " + Math.round(submission.getScore()) + "%." + feedback, null);
        return submissionService.findById(submissionId);
    }

    private List<String> splitOptions(String options) {
        if (options == null || options.isBlank()) {
            return List.of();
        }
        return Arrays.stream(options.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    private boolean matches(String studentAnswer, String correctAnswer) {
        if (studentAnswer == null || correctAnswer == null) {
            return false;
        }
        return studentAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
    }
}
