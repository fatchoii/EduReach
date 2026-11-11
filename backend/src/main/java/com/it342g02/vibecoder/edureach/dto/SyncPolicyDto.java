package com.it342g02.vibecoder.edureach.dto;


public record SyncPolicyDto(
        Long id,
        Boolean autoSyncOnWifi,
        Integer maxPackageMb) {
}
