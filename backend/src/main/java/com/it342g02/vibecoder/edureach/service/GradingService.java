package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.AnswerItem;
import com.it342g02.vibecoder.edureach.dto.AnswerResultDto;
import com.it342g02.vibecoder.edureach.dto.SubmissionResultDto;
import com.it342g02.vibecoder.edureach.dto.SubmitRequest;
import com.it342g02.vibecoder.edureach.entity.Assessment;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Grades a student's answers automatically and stores the submission. */
@Service
public class GradingService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionBankRepository questionBankRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final UserRepository userRepository;

    public GradingService(AssessmentRepository assessmentRepository, QuestionBankRepository questionBankRepository,
                          SubmissionRepository submissionRepository, SubmissionAnswerRepository submissionAnswerRepository,
                          UserRepository userRepository) {
        this.assessmentRepository = assessmentRepository;
        this.questionBankRepository = questionBankRepository;
        this.submissionRepository = submissionRepository;
        this.submissionAnswerRepository = submissionAnswerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SubmissionResultDto submit(Long assessmentId, String email, SubmitRequest request) {
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

        Submission submission = new Submission();
        submission.setAssessment(assessment);
        submission.setUser(student);
        submission.setScore(0.0);
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

        boolean passed = assessment.getPassingScore() == null || score >= assessment.getPassingScore();
        return new SubmissionResultDto(submission.getId(), assessmentId, score, passed, correctCount,
                questions.size(), results);
    }

    private boolean matches(String studentAnswer, String correctAnswer) {
        if (studentAnswer == null || correctAnswer == null) {
            return false;
        }
        return studentAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
    }
}
