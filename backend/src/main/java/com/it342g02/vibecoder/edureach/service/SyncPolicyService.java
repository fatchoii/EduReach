package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.SyncPolicyDto;
import com.it342g02.vibecoder.edureach.entity.SyncPolicy;
import com.it342g02.vibecoder.edureach.repository.SyncPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The single admin-controlled sync/offline policy row. */
@Service
public class SyncPolicyService {

    private final SyncPolicyRepository syncPolicyRepository;

    public SyncPolicyService(SyncPolicyRepository syncPolicyRepository) {
        this.syncPolicyRepository = syncPolicyRepository;
    }

    @Transactional
    public SyncPolicy current() {
        return syncPolicyRepository.findAll().stream().findFirst()
                .orElseGet(() -> syncPolicyRepository.save(new SyncPolicy()));
    }

    @Transactional
    public SyncPolicyDto get() {
        return toDto(current());
    }

    @Transactional
    public SyncPolicyDto update(SyncPolicyDto dto) {
        SyncPolicy policy = current();
        if (dto.autoSyncOnWifi() != null) {
            policy.setAutoSyncOnWifi(dto.autoSyncOnWifi());
        }
        if (dto.maxPackageMb() != null && dto.maxPackageMb() > 0) {
            policy.setMaxPackageMb(dto.maxPackageMb());
        }
        return toDto(syncPolicyRepository.save(policy));
    }

    private SyncPolicyDto toDto(SyncPolicy p) {
        return new SyncPolicyDto(p.getId(), p.getAutoSyncOnWifi(), p.getMaxPackageMb());
    }
}
