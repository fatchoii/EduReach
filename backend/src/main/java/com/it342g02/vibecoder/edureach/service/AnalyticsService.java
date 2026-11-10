package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.SkillMasteryDto;
import com.it342g02.vibecoder.edureach.dto.StudentMasteryDto;
import com.it342g02.vibecoder.edureach.entity.Module;
import com.it342g02.vibecoder.edureach.entity.Submission;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.EnrollmentRepository;
import com.it342g02.vibecoder.edureach.repository.ModuleRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Skill mastery = average submission score per module (each module is one skill). */
@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final ModuleRepository moduleRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public AnalyticsService(ModuleRepository moduleRepository, EnrollmentRepository enrollmentRepository,
                            SubmissionRepository submissionRepository, UserRepository userRepository) {
        this.moduleRepository = moduleRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    public List<StudentMasteryDto> courseMastery(Long courseId) {
        List<Module> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        Map<Long, List<Submission>> byStudent = submissionRepository.findByAssessmentModuleCourseId(courseId).stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getId()));
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(enrollment -> enrollment.getUser())
                .map(student -> buildMastery(student, modules, byStudent.getOrDefault(student.getId(), List.of())))
                .toList();
    }

    public StudentMasteryDto studentMastery(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + studentId));
        List<Submission> submissions = submissionRepository.findByUserId(studentId);
        List<Module> modules = submissions.stream()
                .map(s -> s.getAssessment().getModule())
                .distinct()
                .sorted(Comparator.comparing(Module::getOrderIndex, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        return buildMastery(student, modules, submissions);
    }

    private StudentMasteryDto buildMastery(User student, List<Module> modules, List<Submission> submissions) {
        List<SkillMasteryDto> skills = new ArrayList<>();
        double total = 0;
        int scored = 0;
        for (Module module : modules) {
            List<Double> scores = submissions.stream()
                    .filter(s -> s.getAssessment().getModule().getId().equals(module.getId()))
                    .map(s -> s.getScore() == null ? 0.0 : s.getScore())
                    .toList();
            double average = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            if (!scores.isEmpty()) {
                total += average;
                scored++;
            }
            skills.add(new SkillMasteryDto(module.getId(), module.getTitle(), round(average), scores.size()));
        }
        double overall = scored == 0 ? 0.0 : round(total / scored);
        return new StudentMasteryDto(student.getId(), student.getFullName(), overall, skills);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
