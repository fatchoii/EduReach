package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.entity.Assessment;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.Enrollment;
import com.it342g02.vibecoder.edureach.entity.LiveSession;
import com.it342g02.vibecoder.edureach.entity.Notification;
import com.it342g02.vibecoder.edureach.entity.NotificationSetting;
import com.it342g02.vibecoder.edureach.entity.Role;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.repository.AssessmentRepository;
import com.it342g02.vibecoder.edureach.repository.EnrollmentRepository;
import com.it342g02.vibecoder.edureach.repository.NotificationRepository;
import com.it342g02.vibecoder.edureach.repository.NotificationSettingRepository;
import com.it342g02.vibecoder.edureach.repository.SubmissionRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates in-app notifications (deadlines, new content, live session invites, grades) and, when the
 * learner enabled email alerts and SMTP is configured, also emails them.
 */
@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);
    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository settingRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssessmentRepository assessmentRepository;
    private final SubmissionRepository submissionRepository;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String mailFrom;

    public AlertService(NotificationRepository notificationRepository, NotificationSettingRepository settingRepository,
                        EnrollmentRepository enrollmentRepository, AssessmentRepository assessmentRepository,
                        SubmissionRepository submissionRepository, ObjectProvider<JavaMailSender> mailSender,
                        @Value("${spring.mail.username:}") String mailFrom) {
        this.notificationRepository = notificationRepository;
        this.settingRepository = settingRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.assessmentRepository = assessmentRepository;
        this.submissionRepository = submissionRepository;
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
    }

    /** Returns true when a notification was created (false when skipped: duplicate or switched off). */
    @Transactional
    public boolean notifyUser(User user, String type, String title, String message, String refKey) {
        if (user == null) {
            return false;
        }
        if (refKey != null && notificationRepository.existsByUserIdAndRefKey(user.getId(), refKey)) {
            return false;
        }
        NotificationSetting setting = settingRepository.findFirstByUserId(user.getId()).orElse(null);
        if (!wants(setting, type)) {
            return false;
        }
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRefKey(refKey);
        notification.setRead(false);
        notificationRepository.save(notification);
        if (setting != null && Boolean.TRUE.equals(setting.getEmailAlerts())) {
            sendEmail(user.getEmail(), title, message);
        }
        return true;
    }

    @Transactional
    public void notifyCourseLearners(Course course, String type, String title, String message, String refKey) {
        for (Enrollment enrollment : enrollmentRepository.findByCourseId(course.getId())) {
            User learner = enrollment.getUser();
            if (learner.getRole() == Role.LEARNER) {
                notifyUser(learner, type, title, message, refKey);
            }
        }
    }

    @Transactional
    public void onLiveSessionScheduled(LiveSession session) {
        String when = session.getScheduledAt() == null ? "a date to be announced" : session.getScheduledAt().format(WHEN);
        String link = session.getMeetingUrl() == null || session.getMeetingUrl().isBlank()
                ? "" : " Join here: " + session.getMeetingUrl();
        notifyCourseLearners(session.getCourse(), "LIVE_SESSION", "Live session invite: " + session.getTitle(),
                session.getCourse().getTitle() + " - scheduled for " + when + "." + link, "LIVE:" + session.getId());
    }

    /** Reminds learners who have not submitted an assessment that is due soon. Returns how many were sent. */
    @Transactional
    public int runDeadlineReminders() {
        LocalDate today = LocalDate.now();
        int created = 0;
        for (Assessment assessment : assessmentRepository.findByStatus("ACTIVE")) {
            LocalDate due = assessment.getDueDate();
            if (due == null || due.isBefore(today) || due.isAfter(today.plusDays(14))) {
                continue;
            }
            long daysLeft = ChronoUnit.DAYS.between(today, due);
            Course course = assessment.getModule().getCourse();
            for (Enrollment enrollment : enrollmentRepository.findByCourseId(course.getId())) {
                User learner = enrollment.getUser();
                if (learner.getRole() != Role.LEARNER) {
                    continue;
                }
                NotificationSetting setting = settingRepository.findFirstByUserId(learner.getId()).orElse(null);
                int daysBefore = setting == null || setting.getReminderDaysBefore() == null ? 2 : setting.getReminderDaysBefore();
                if (daysLeft > daysBefore) {
                    continue;
                }
                if (!submissionRepository.findByAssessmentIdAndUserId(assessment.getId(), learner.getId()).isEmpty()) {
                    continue;
                }
                String when = daysLeft == 0 ? "today" : daysLeft == 1 ? "tomorrow" : "in " + daysLeft + " days";
                boolean sent = notifyUser(learner, "DEADLINE", "Due " + when + ": " + assessment.getTitle(),
                        assessment.getTitle() + " (" + course.getTitle() + ") is due " + when + ".",
                        "DEADLINE:" + assessment.getId() + ":" + due);
                if (sent) {
                    created++;
                }
            }
        }
        return created;
    }

    private boolean wants(NotificationSetting setting, String type) {
        if (setting == null) {
            return true;
        }
        return switch (type) {
            case "DEADLINE" -> Boolean.TRUE.equals(setting.getDeadlineReminders());
            case "NEW_CONTENT" -> Boolean.TRUE.equals(setting.getNewContentAlerts());
            case "LIVE_SESSION" -> Boolean.TRUE.equals(setting.getLiveSessionInvites());
            default -> true;
        };
    }

    private void sendEmail(String to, String subject, String body) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                SimpleMailMessage mail = new SimpleMailMessage();
                if (!mailFrom.isBlank()) {
                    mail.setFrom(mailFrom);
                }
                mail.setTo(to);
                mail.setSubject("[EduReach] " + subject);
                mail.setText(body);
                sender.send(mail);
            } catch (Exception ex) {
                log.warn("Could not send email to {}: {}", to, ex.getMessage());
            }
        });
    }
}
