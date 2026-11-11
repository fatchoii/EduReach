package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.EnrollmentDto;
import com.it342g02.vibecoder.edureach.dto.NotificationDto;
import com.it342g02.vibecoder.edureach.dto.NotificationSettingDto;
import com.it342g02.vibecoder.edureach.dto.SubmissionDto;
import com.it342g02.vibecoder.edureach.entity.Course;
import com.it342g02.vibecoder.edureach.entity.Enrollment;
import com.it342g02.vibecoder.edureach.entity.Notification;
import com.it342g02.vibecoder.edureach.entity.NotificationSetting;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.CourseRepository;
import com.it342g02.vibecoder.edureach.repository.EnrollmentRepository;
import com.it342g02.vibecoder.edureach.repository.NotificationRepository;
import com.it342g02.vibecoder.edureach.repository.NotificationSettingRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Everything a signed-in user does with their own data (/api/me/...). */
@Service
public class LearnerService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository settingRepository;
    private final SubmissionService submissionService;
    private final NotificationService notificationService;
    private final EnrollmentService enrollmentService;

    public LearnerService(UserRepository userRepository, CourseRepository courseRepository,
                          EnrollmentRepository enrollmentRepository, NotificationRepository notificationRepository,
                          NotificationSettingRepository settingRepository, SubmissionService submissionService,
                          NotificationService notificationService, EnrollmentService enrollmentService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.notificationRepository = notificationRepository;
        this.settingRepository = settingRepository;
        this.submissionService = submissionService;
        this.notificationService = notificationService;
        this.enrollmentService = enrollmentService;
    }

    @Transactional(readOnly = true)
    public List<SubmissionDto> mySubmissions(String email) {
        return submissionService.findAll(null, userId(email));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> myNotifications(String email) {
        return notificationService.findAll(userId(email)).stream()
                .sorted(Comparator.comparing(NotificationDto::createdAt,
                        Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder())))
                .toList();
    }

    @Transactional
    public void markRead(String email, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id " + notificationId));
        if (!notification.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("This notification belongs to another user");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllRead(String email) {
        for (Notification notification : notificationRepository.findByUserId(userId(email))) {
            if (!Boolean.TRUE.equals(notification.getRead())) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
    }

    @Transactional
    public EnrollmentDto enroll(String email, Long courseId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + courseId));
        if (!"PUBLISHED".equals(course.getStatus())) {
            throw new ResourceNotFoundException("Course is not published");
        }
        if (enrollmentRepository.findByUserIdAndCourseId(user.getId(), courseId).isEmpty()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setUser(user);
            enrollment.setCourse(course);
            enrollmentRepository.save(enrollment);
        }
        return enrollmentService.findAll(user.getId(), courseId).get(0);
    }

    @Transactional
    public NotificationSettingDto getSettings(String email) {
        return toDto(settingFor(email));
    }

    @Transactional
    public NotificationSettingDto updateSettings(String email, NotificationSettingDto dto) {
        NotificationSetting setting = settingFor(email);
        if (dto.deadlineReminders() != null) {
            setting.setDeadlineReminders(dto.deadlineReminders());
        }
        if (dto.newContentAlerts() != null) {
            setting.setNewContentAlerts(dto.newContentAlerts());
        }
        if (dto.liveSessionInvites() != null) {
            setting.setLiveSessionInvites(dto.liveSessionInvites());
        }
        if (dto.emailAlerts() != null) {
            setting.setEmailAlerts(dto.emailAlerts());
        }
        if (dto.reminderDaysBefore() != null && dto.reminderDaysBefore() >= 0) {
            setting.setReminderDaysBefore(dto.reminderDaysBefore());
        }
        return toDto(settingRepository.save(setting));
    }

    private NotificationSetting settingFor(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return settingRepository.findFirstByUserId(user.getId()).orElseGet(() -> {
            NotificationSetting created = new NotificationSetting();
            created.setUser(user);
            return settingRepository.save(created);
        });
    }

    private NotificationSettingDto toDto(NotificationSetting s) {
        return new NotificationSettingDto(s.getId(), s.getUser().getId(), s.getDeadlineReminders(),
                s.getNewContentAlerts(), s.getLiveSessionInvites(), s.getEmailAlerts(), s.getReminderDaysBefore());
    }

    private Long userId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")).getId();
    }
}
