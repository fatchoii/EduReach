package com.it342g02.vibecoder.edureach.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "notification_settings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_setting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private Boolean deadlineReminders = true;

    private Boolean newContentAlerts = true;

    private Boolean liveSessionInvites = true;

    private Boolean emailAlerts = false;

    private Integer reminderDaysBefore = 2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getDeadlineReminders() {
        return deadlineReminders;
    }

    public void setDeadlineReminders(Boolean deadlineReminders) {
        this.deadlineReminders = deadlineReminders;
    }

    public Boolean getNewContentAlerts() {
        return newContentAlerts;
    }

    public void setNewContentAlerts(Boolean newContentAlerts) {
        this.newContentAlerts = newContentAlerts;
    }

    public Boolean getLiveSessionInvites() {
        return liveSessionInvites;
    }

    public void setLiveSessionInvites(Boolean liveSessionInvites) {
        this.liveSessionInvites = liveSessionInvites;
    }

    public Boolean getEmailAlerts() {
        return emailAlerts;
    }

    public void setEmailAlerts(Boolean emailAlerts) {
        this.emailAlerts = emailAlerts;
    }

    public Integer getReminderDaysBefore() {
        return reminderDaysBefore;
    }

    public void setReminderDaysBefore(Integer reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }

}
