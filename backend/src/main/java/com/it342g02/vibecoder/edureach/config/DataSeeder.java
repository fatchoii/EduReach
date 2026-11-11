package com.it342g02.vibecoder.edureach.config;

import com.it342g02.vibecoder.edureach.entity.Assessment;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.Enrollment;
import com.it342g02.vibecoder.edureach.entity.Lesson;
import com.it342g02.vibecoder.edureach.entity.LiveSession;
import com.it342g02.vibecoder.edureach.entity.Module;
import com.it342g02.vibecoder.edureach.entity.ParentStudent;
import com.it342g02.vibecoder.edureach.entity.QuestionBank;
import com.it342g02.vibecoder.edureach.entity.Role;
import com.it342g02.vibecoder.edureach.entity.Submission;
import com.it342g02.vibecoder.edureach.entity.SubmissionAnswer;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.EnrollmentRepository;
import com.it342g02.vibecoder.edureach.repository.LessonRepository;
import com.it342g02.vibecoder.edureach.repository.LiveSessionRepository;
import com.it342g02.vibecoder.edureach.repository.ModuleRepository;
import com.it342g02.vibecoder.edureach.repository.ParentStudentRepository;
import com.it342g02.vibecoder.edureach.repository.QuestionBankRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionAnswerRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import com.it342g02.vibecoder.edureach.service.AlertService;
import com.it342g02.vibecoder.edureach.service.ProgressService;
import com.it342g02.vibecoder.edureach.service.SyncPolicyService;
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
    /** Starting mastery per learner for each of the five modules. */
    private static final int[][] MASTERY = {
            {95, 80, 90, 70, 62}, {88, 70, 84, 60, 50}, {98, 92, 95, 85, 78},
            {80, 58, 72, 48, 35}, {92, 85, 88, 75, 66}, {85, 66, 80, 55, 44}
    };
    /** {question, answer, module index, difficulty, options separated by | (empty = fill in)} */
    private static final String[][] QUESTIONS = {
            {"23 + 15 = ?", "38", "0", "EASY", "36|37|38|39"}, {"45 + 32 = ?", "77", "0", "EASY", ""},
            {"36 + 28 = ?", "64", "1", "MEDIUM", "54|64|74|84"}, {"57 + 39 = ?", "96", "1", "HARD", ""},
            {"68 - 25 = ?", "43", "2", "EASY", "33|43|53|63"}, {"45 - 17 = ?", "28", "3", "MEDIUM", ""},
            {"72 - 38 = ?", "34", "3", "MEDIUM", ""}, {"60 - 27 = ?", "33", "4", "MEDIUM", ""},
            {"300 - 148 = ?", "152", "4", "HARD", ""}, {"405 - 167 = ?", "238", "4", "HARD", ""}
    };

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final AssessmentRepository assessmentRepository;
    private final QuestionBankRepository questionBankRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final ParentStudentRepository parentStudentRepository;
    private final LiveSessionRepository liveSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProgressService progressService;
    private final AlertService alertService;
    private final SyncPolicyService syncPolicyService;

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository,
                      EnrollmentRepository enrollmentRepository, ModuleRepository moduleRepository,
                      LessonRepository lessonRepository, AssessmentRepository assessmentRepository,
                      QuestionBankRepository questionBankRepository, SubmissionRepository submissionRepository,
                      SubmissionAnswerRepository submissionAnswerRepository,
                      ParentStudentRepository parentStudentRepository, LiveSessionRepository liveSessionRepository,
                      PasswordEncoder passwordEncoder, ProgressService progressService, AlertService alertService,
                      SyncPolicyService syncPolicyService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.assessmentRepository = assessmentRepository;
        this.questionBankRepository = questionBankRepository;
        this.submissionRepository = submissionRepository;
        this.submissionAnswerRepository = submissionAnswerRepository;
        this.parentStudentRepository = parentStudentRepository;
        this.liveSessionRepository = liveSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.progressService = progressService;
        this.alertService = alertService;
        this.syncPolicyService = syncPolicyService;
    }

    @Override
    public void run(String... args) {
        syncPolicyService.current();
        if (userRepository.count() > 0) {
            return;
        }
        String hash = passwordEncoder.encode(DEMO_PASSWORD);

        saveUser("System Admin", "admin@edureach.com", Role.ADMIN, hash);
        User instructor = saveUser("Ms. Santos", "teacher@edureach.com", Role.INSTRUCTOR, hash);
        User parent = saveUser("Maria Reyes", "parent@edureach.com", Role.PARENT, hash);
        List<User> learners = new ArrayList<>();
        for (int i = 0; i < STUDENT_NAMES.length; i++) {
            learners.add(saveUser(STUDENT_NAMES[i], STUDENT_EMAILS[i], Role.LEARNER, hash));
        }

        ParentStudent link = new ParentStudent();
        link.setParent(parent);
        link.setStudent(learners.get(0));
        parentStudentRepository.save(link);

        Course sampaguita = saveCourse(instructor, "Grade 3 - Sampaguita");
        Course orchid = saveCourse(instructor, "Grade 3 - Orchid");
        for (int i = 0; i < learners.size(); i++) {
            enroll(learners.get(i), sampaguita);
            if (i < 4) {
                enroll(learners.get(i), orchid);
            }
        }

        List<Module> modules = saveModules(sampaguita);
        List<Module> orchidModules = saveModules(orchid);

        // Lessons: text, video, audio and slides (the media links are public sample files; replace them with your own)
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(saveLesson(modules.get(0), "Adding Two-Digit Numbers", "TEXT", null,
                "Add the ones first, then the tens. Example: 23 + 15. The ones are 3 + 5 = 8 and the tens are 2 + 1 = 3, so the answer is 38.",
                5, "PUBLISHED"));
        lessons.add(saveLesson(modules.get(0), "Adding on a Number Line (Video)", "VIDEO",
                "https://www.w3schools.com/html/mov_bbb.mp4", "Watch how jumps on a number line show addition.", 4, "PUBLISHED"));
        lessons.add(saveLesson(modules.get(1), "Carrying to the Tens", "TEXT", null,
                "When the ones add up to 10 or more, carry 1 ten to the tens column. Example: 36 + 28. 6 + 8 = 14, write 4 and carry 1. Then 3 + 2 + 1 = 6, so the answer is 64.",
                6, "PUBLISHED"));
        lessons.add(saveLesson(modules.get(1), "Regrouping Song (Audio)", "AUDIO",
                "https://www.w3schools.com/html/horse.mp3", "Listen and sing along to remember the regrouping steps.", 3, "PUBLISHED"));
        lessons.add(saveLesson(modules.get(2), "Basic Subtraction (Slides)", "SLIDES",
                "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", "Slides on taking away.", 8, "PUBLISHED"));
        saveLesson(modules.get(3), "Borrowing from the Tens", "TEXT", null,
                "Trade 1 ten for 10 ones, then subtract. Example: 45 - 17.", 6, "DRAFT");

        // One assessment per module (module 4 is an assignment the instructor reviews by hand)
        List<Assessment> assessments = new ArrayList<>();
        for (int i = 0; i < modules.size(); i++) {
            assessments.add(saveAssessment(modules.get(i), modules.get(i).getTitle() + (i == 3 ? " Assignment" : " Practice"),
                    i == 3 ? "ASSIGNMENT" : "QUIZ", LocalDate.now().plusDays(7)));
        }
        List<QuestionBank> assignmentQuestions = new ArrayList<>();
        for (String[] q : QUESTIONS) {
            int moduleIndex = Integer.parseInt(q[2]);
            QuestionBank question = saveQuestion(assessments.get(moduleIndex), q[0], q[1], q[3], q[4]);
            if (moduleIndex == 3) {
                assignmentQuestions.add(question);
            }
        }

        // An Orchid quiz due tomorrow with no submissions, so deadline reminders can be demonstrated
        Assessment warmup = saveAssessment(orchidModules.get(0), "Orchid Warm-up Quiz", "QUIZ", LocalDate.now().plusDays(1));
        saveQuestion(warmup, "10 + 15 = ?", "25", "EASY", "15|20|25|30");
        saveQuestion(warmup, "21 + 32 = ?", "53", "EASY", "");

        // Past submissions. Three assignment submissions wait in the instructor's review queue.
        for (int s = 0; s < learners.size(); s++) {
            for (int m = 0; m < assessments.size(); m++) {
                boolean pending = m == 3 && (s == 1 || s == 3 || s == 5);
                Submission submission = new Submission();
                submission.setAssessment(assessments.get(m));
                submission.setUser(learners.get(s));
                submission.setScore((double) MASTERY[s][m]);
                submission.setSubmittedAt(LocalDateTime.now().minusDays(m + 1L));
                submission.setStatus(pending ? "PENDING_REVIEW" : "GRADED");
                submission = submissionRepository.save(submission);
                if (pending) {
                    String[][] given = {{"28", "44"}, {"27", "44"}, {"28", "34"}};
                    String[] answers = given[s == 1 ? 0 : s == 3 ? 1 : 2];
                    for (int q = 0; q < assignmentQuestions.size(); q++) {
                        SubmissionAnswer answer = new SubmissionAnswer();
                        answer.setSubmission(submission);
                        answer.setQuestion(assignmentQuestions.get(q));
                        answer.setStudentAnswer(answers[q]);
                        answer.setCorrect(answers[q].equals(assignmentQuestions.get(q).getCorrectAnswer()));
                        submissionAnswerRepository.save(answer);
                    }
                }
            }
        }

        // Some lesson progress, then recalculate everyone's course progress
        progressService.markComplete(learners.get(0), lessons.get(0), null);
        progressService.markComplete(learners.get(0), lessons.get(1), null);
        progressService.markComplete(learners.get(1), lessons.get(0), null);
        for (User learner : learners) {
            progressService.recalculate(learner, sampaguita);
            progressService.recalculate(learner, orchid);
        }

        // A live session and a few alerts
        LiveSession session = new LiveSession();
        session.setCourse(sampaguita);
        session.setTitle("Live Review: Regrouping");
        session.setDescription("Practice carrying and regrouping together.");
        session.setScheduledAt(LocalDate.now().plusDays(1).atTime(15, 0));
        session.setMeetingUrl("https://meet.google.com");
        session = liveSessionRepository.save(session);
        alertService.onLiveSessionScheduled(session);
        alertService.notifyCourseLearners(sampaguita, "NEW_CONTENT", "New lesson: " + lessons.get(0).getTitle(),
                "A new lesson was added to " + sampaguita.getTitle() + ".", "SEED:LESSON:" + lessons.get(0).getId());

        log.info("Demo data created. Password for every account: {}. Accounts: admin@edureach.com, teacher@edureach.com, "
                + "parent@edureach.com, ana@edureach.com (learner)", DEMO_PASSWORD);
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
        course.setStatus("PUBLISHED");
        return courseRepository.save(course);
    }

    private void enroll(User learner, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(learner);
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

    private Lesson saveLesson(Module module, String title, String type, String url, String text, int minutes, String status) {
        Lesson lesson = new Lesson();
        lesson.setModule(module);
        lesson.setTitle(title);
        lesson.setContentType(type);
        lesson.setContentUrl(url);
        lesson.setTextContent(text);
        lesson.setDescription(text.length() > 80 ? text.substring(0, 80) + "..." : text);
        lesson.setDurationMinutes(minutes);
        lesson.setStatus(status);
        return lessonRepository.save(lesson);
    }

    private Assessment saveAssessment(Module module, String title, String type, LocalDate due) {
        Assessment assessment = new Assessment();
        assessment.setModule(module);
        assessment.setTitle(title);
        assessment.setType(type);
        assessment.setPassingScore(75.0);
        assessment.setDueDate(due);
        assessment.setStatus("ACTIVE");
        return assessmentRepository.save(assessment);
    }

    private QuestionBank saveQuestion(Assessment assessment, String text, String answer, String difficulty, String options) {
        QuestionBank question = new QuestionBank();
        question.setAssessment(assessment);
        question.setQuestionText(text);
        question.setCorrectAnswer(answer);
        question.setDifficulty(difficulty);
        question.setPoints(1);
        if (options == null || options.isBlank()) {
            question.setQuestionType("FILL_IN");
        } else {
            question.setQuestionType("MULTIPLE_CHOICE");
            question.setOptions(options);
        }
        return questionBankRepository.save(question);
    }
}
