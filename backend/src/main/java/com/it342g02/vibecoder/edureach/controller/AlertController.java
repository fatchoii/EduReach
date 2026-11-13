package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.ReminderResultDto;
import com.it342g02.vibecoder.edureach.service.AlertService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    /** Runs the deadline-reminder check right now (it also runs automatically every hour). */
    @PostMapping("/run-reminders")
    public ReminderResultDto runReminders() {
        return new ReminderResultDto(alertService.runDeadlineReminders());
    }
}
