package com.it342g02.vibecoder.edureach.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sync_policy")
public class SyncPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sync_policy_id")
    private Long id;

    private Boolean autoSyncOnWifi = true;

    private Integer maxPackageMb = 50;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAutoSyncOnWifi() {
        return autoSyncOnWifi;
    }

    public void setAutoSyncOnWifi(Boolean autoSyncOnWifi) {
        this.autoSyncOnWifi = autoSyncOnWifi;
    }

    public Integer getMaxPackageMb() {
        return maxPackageMb;
    }

    public void setMaxPackageMb(Integer maxPackageMb) {
        this.maxPackageMb = maxPackageMb;
    }

}
