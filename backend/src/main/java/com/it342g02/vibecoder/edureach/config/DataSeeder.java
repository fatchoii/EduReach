package com.it342g02.vibecoder.edureach.config;

import com.it342g02.vibecoder.edureach.entity.Assessment;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.Enrollment;
import com.it342g02.vibecoder.edureach.entity.Lesson;
import com.it342g02.vibecoder.edureach.entity.Module;
import com.it342g02.vibecoder.edureach.entity.ParentStudent;
import com.it342g02.vibecoder.edureach.entity.QuestionBank;
import com.it342g02.vibecoder.edureach.entity.Role;
import com.it342g02.vibecoder.edureach.entity.Submission;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.EnrollmentRepository;
import com.it342g02.vibecoder.edureach.repository.LessonRepository;
import com.it342g02.vibecoder.edureach.repository.ModuleRepository;
import com.it342g02.vibecoder.edureach.repository.ParentStudentRepository;
import com.it342g02.vibecoder.edureach.repository.QuestionBankRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Inserts demo data on first start (only when the users table is empty). */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String DEMO_PASSWORD = "password123";

    private static final String[] MODULE_TITLES = {
            "Basic Addition", "Addition with Regrouping", "Basic Subtraction",
            "Subtraction with Borrowing", "Borrowing Across Zero"
    };
    private static final String[] STUDENT_NAMES = {"Ana Reyes", "Ben Lopez", "Carla Tan", "Dan Cruz", "Ella Go", "Finn Uy"};
    private static final String[] STUDENT_EMAILS = {
            "ana@edureach.com", "ben@edureach.com", "carla@edureach.com",
            "dan@edureach.com", "ella@edureach.com", "finn@edureach.com"
    };
    /** Starting mastery per student for each of the five modules. */
    private static final int[][] MASTERY = {
            {95, 80, 90, 70, 62}, {88, 70, 84, 60, 50}, {98, 92, 95, 85, 78},
            {80, 58, 72, 48, 35}, {92, 85, 88, 75, 66}, {85, 66, 80, 55, 44}
    };
    /** {question, answer, module index, difficulty} */
    private static final String[][] QUESTIONS = {
            {"23 + 15 = ?", "38", "0", "EASY"}, {"45 + 32 = ?", "77", "0", "EASY"},
            {"36 + 28 = ?", "64", "1", "MEDIUM"}, {"57 + 39 = ?", "96", "1", "HARD"},
            {"68 - 25 = ?", "43", "2", "EASY"}, {"45 - 17 = ?", "28", "3", "MEDIUM"},
            {"72 - 38 = ?", "34", "3", "MEDIUM"}, {"60 - 27 = ?", "33", "4", "MEDIUM"},
            {"300 - 148 = ?", "152", "4", "HARD"}, {"405 - 167 = ?", "238", "4", "HARD"}
    };

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final AssessmentRepository assessmentRepository;
    private final QuestionBankRepository questionBankRepository;
    private final SubmissionRepository submissionRepository;
    private final ParentStudentRepository parentStudentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository,
                      EnrollmentRepository enrollmentRepository, ModuleRepository moduleRepository,
                      LessonRepository lessonRepository, AssessmentRepository assessmentRepository,
                      QuestionBankRepository questionBankRepository, SubmissionRepository submissionRepository,
                      ParentStudentRepository parentStudentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.assessmentRepository = assessmentRepository;
        this.questionBankRepository = questionBankRepository;
        this.submissionRepository = submissionRepository;
        this.parentStudentRepository = parentStudentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        String hash = passwordEncoder.encode(DEMO_PASSWORD);

        User teacher = saveUser("Ms. Santos", "teacher@edureach.com", Role.TEACHER, hash);
        User parent = saveUser("Maria Reyes", "parent@edureach.com", Role.PARENT, hash);
        List<User> students = new ArrayList<>();
        for (int i = 0; i < STUDENT_NAMES.length; i++) {
            students.add(saveUser(STUDENT_NAMES[i], STUDENT_EMAILS[i], Role.STUDENT, hash));
        }

        ParentStudent link = new ParentStudent();
        link.setParent(parent);
        link.setStudent(students.get(0));
        parentStudentRepository.save(link);

        Course sampaguita = saveCourse(teacher, "Grade 3 - Sampaguita");
        Course orchid = saveCourse(teacher, "Grade 3 - Orchid");
        for (int i = 0; i < students.size(); i++) {
            enroll(students.get(i), sampaguita);
            if (i < 4) {
                enroll(students.get(i), orchid);
            }
        }

        List<Module> sampaguitaModules = saveModules(sampaguita);
        saveModules(orchid);

        saveLesson(sampaguitaModules.get(0), "Adding Two-Digit Numbers", "Add tens and ones.",
                "Use the number line to add tens, then ones.", "PUBLISHED");
        saveLesson(sampaguitaModules.get(1), "Carrying to the Tens", "Regroup 10 ones as 1 ten.",
                "Use base-10 blocks to regroup.", "PUBLISHED");
        saveLesson(sampaguitaModules.get(3), "Borrowing from the Tens", "Trade 1 ten for 10 ones.",
                "Break a ten rod into 10 ones.", "DRAFT");

        List<Assessment> assessments = new ArrayList<>();
        for (Module module : sampaguitaModules) {
            Assessment assessment = new Assessment();
            assessment.setModule(module);
            assessment.setTitle(module.getTitle() + " Practice");
            assessment.setType("QUIZ");
            assessment.setPassingScore(75.0);
            assessment.setDueDate(LocalDate.now().plusDays(7));
            assessment.setStatus("ACTIVE");
            assessments.add(assessmentRepository.save(assessment));
        }

        for (String[] q : QUESTIONS) {
            QuestionBank question = new QuestionBank();
            question.setAssessment(assessments.get(Integer.parseInt(q[2])));
            question.setQuestionText(q[0]);
            question.setQuestionType("FILL_IN");
            question.setPoints(1);
            question.setCorrectAnswer(q[1]);
            question.setDifficulty(q[3]);
            questionBankRepository.save(question);
        }

        for (int s = 0; s < students.size(); s++) {
            for (int m = 0; m < assessments.size(); m++) {
                Submission submission = new Submission();
                submission.setAssessment(assessments.get(m));
                submission.setUser(students.get(s));
                submission.setScore((double) MASTERY[s][m]);
                submission.setSubmittedAt(LocalDateTime.now().minusDays(m + 1L));
                submissionRepository.save(submission);
            }
        }

        log.info("Demo data created. Log in with teacher@edureach.com, parent@edureach.com or ana@edureach.com (password: {})",
                DEMO_PASSWORD);
    }

    private User saveUser(String name, String email, Role role, String hash) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPasswordHash(hash);
        user.setRole(role);
        return userRepository.save(user);
    }

    private Course saveCourse(User instructor, String title) {
        Course course = new Course();
        course.setInstructor(instructor);
        course.setTitle(title);
        course.setDescription("Addition and subtraction practice for Grade 3 learners.");
        course.setCategory("Mathematics");
        course.setLanguage("English");
        return courseRepository.save(course);
    }

    private void enroll(User student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);
    }

    private List<Module> saveModules(Course course) {
        List<Module> modules = new ArrayList<>();
        for (int i = 0; i < MODULE_TITLES.length; i++) {
            Module module = new Module();
            module.setCourse(course);
            module.setTitle(MODULE_TITLES[i]);
            module.setOrderIndex(i + 1);
            modules.add(moduleRepository.save(module));
        }
        return modules;
    }

    private void saveLesson(Module module, String title, String description, String instructions, String status) {
        Lesson lesson = new Lesson();
        lesson.setModule(module);
        lesson.setTitle(title);
        lesson.setDescription(description);
        lesson.setInstructions(instructions);
        lesson.setStatus(status);
        lessonRepository.save(lesson);
    }
}
