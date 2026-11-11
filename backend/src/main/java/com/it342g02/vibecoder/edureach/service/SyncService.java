package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.SubmissionResultDto;
import com.it342g02.vibecoder.edureach.dto.SubmitRequest;
import com.it342g02.vibecoder.edureach.dto.SyncEvent;
import com.it342g02.vibecoder.edureach.dto.SyncLogDto;
import com.it342g02.vibecoder.edureach.dto.SyncRequest;
import com.it342g02.vibecoder.edureach.dto.SyncResponse;
import com.it342g02.vibecoder.edureach.dto.SyncResultDto;
import com.it342g02.vibecoder.edureach.entity.SyncLog;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.SyncLogRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Receives what a learner did while offline (lessons completed, quizzes taken) and applies it.
 * Every event has a client-generated id, so retrying a sync never counts anything twice.
 * Not transactional on purpose: each event succeeds or fails on its own.
 */
@Service
public class SyncService {

    private final UserRepository userRepository;
    private final SyncLogRepository syncLogRepository;
    private final ProgressService progressService;
    private final GradingService gradingService;

    public SyncService(UserRepository userRepository, SyncLogRepository syncLogRepository,
                       ProgressService progressService, GradingService gradingService) {
        this.userRepository = userRepository;
        this.syncLogRepository = syncLogRepository;
        this.progressService = progressService;
        this.gradingService = gradingService;
    }

    public SyncResponse process(String email, SyncRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<SyncResultDto> results = new ArrayList<>();
        int synced = 0;
        int duplicates = 0;
        int failed = 0;

        for (SyncEvent event : request.events()) {
            if (event.clientId() == null || event.clientId().isBlank()) {
                failed++;
                results.add(new SyncResultDto(null, "FAILED", "clientId is required", null, null));
                continue;
            }
            if (syncLogRepository.existsByUserIdAndClientId(user.getId(), event.clientId())) {
                duplicates++;
                results.add(new SyncResultDto(event.clientId(), "DUPLICATE", "Already synced", null, null));
                continue;
            }
            try {
                SyncResultDto result = handle(email, event);
                record(user, request.deviceId(), event, "SYNCED", result.message());
                synced++;
                results.add(result);
            } catch (RuntimeException ex) {
                String message = ex.getMessage() == null ? "Unexpected error" : ex.getMessage();
                record(user, request.deviceId(), event, "FAILED", message);
                failed++;
                results.add(new SyncResultDto(event.clientId(), "FAILED", message, null, null));
            }
        }
        return new SyncResponse(synced, duplicates, failed, results);
    }

    @Transactional(readOnly = true)
    public List<SyncLogDto> allLogs() {
        return syncLogRepository.findAllByOrderBySyncedAtDesc().stream()
                .map(l -> new SyncLogDto(l.getId(), l.getUser().getId(), l.getDeviceId(), l.getEventType(),
                        l.getClientId(), l.getStatus(), l.getMessage(), l.getOccurredAt(), l.getSyncedAt(),
                        l.getUser().getFullName()))
                .toList();
    }

    private SyncResultDto handle(String email, SyncEvent event) {
        String type = event.type() == null ? "" : event.type();
        switch (type) {
            case "LESSON_COMPLETE": {
                if (event.lessonId() == null) {
                    throw new IllegalArgumentException("lessonId is required");
                }
                progressService.completeLesson(event.lessonId(), email);
                return new SyncResultDto(event.clientId(), "SYNCED", "Lesson progress saved", null, null);
            }
            case "QUIZ_SUBMIT": {
                if (event.assessmentId() == null || event.answers() == null) {
                    throw new IllegalArgumentException("assessmentId and answers are required");
                }
                SubmissionResultDto graded = gradingService.submit(event.assessmentId(), email,
                        new SubmitRequest(event.answers()), event.occurredAt());
                return new SyncResultDto(event.clientId(), "SYNCED", "Quiz graded", graded.submissionId(), graded.score());
            }
            default:
                throw new IllegalArgumentException("Unknown event type: " + type);
        }
    }

    private void record(User user, String deviceId, SyncEvent event, String status, String message) {
        SyncLog log = new SyncLog();
        log.setUser(user);
        log.setDeviceId(deviceId);
        log.setEventType(event.type());
        log.setClientId(event.clientId());
        log.setStatus(status);
        log.setMessage(message == null ? null : message.substring(0, Math.min(message.length(), 900)));
        log.setOccurredAt(event.occurredAt() == null ? LocalDateTime.now() : event.occurredAt());
        syncLogRepository.save(log);
    }
}
