package com.it342g02.vibecoder.edureach.config;

import com.it342g02.vibecoder.edureach.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Checks for upcoming deadlines every hour (first run one minute after startup). */
@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final AlertService alertService;

    public ReminderScheduler(AlertService alertService) {
        this.alertService = alertService;
    }

    @Scheduled(fixedDelay = 3600000, initialDelay = 60000)
    public void sendDeadlineReminders() {
        try {
            int created = alertService.runDeadlineReminders();
            if (created > 0) {
                log.info("Deadline reminders created: {}", created);
            }
        } catch (RuntimeException ex) {
            log.warn("Deadline reminder run failed: {}", ex.getMessage());
        }
    }
}
