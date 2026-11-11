package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.SyncLogDto;
import com.it342g02.vibecoder.edureach.dto.SyncPolicyDto;
import com.it342g02.vibecoder.edureach.dto.SyncRequest;
import com.it342g02.vibecoder.edureach.dto.SyncResponse;
import com.it342g02.vibecoder.edureach.service.SyncPolicyService;
import com.it342g02.vibecoder.edureach.service.SyncService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SyncController {

    private final SyncService syncService;
    private final SyncPolicyService syncPolicyService;

    public SyncController(SyncService syncService, SyncPolicyService syncPolicyService) {
        this.syncService = syncService;
        this.syncPolicyService = syncPolicyService;
    }

    /** Uploads progress and quiz results collected while offline. */
    @PostMapping("/sync")
    public SyncResponse sync(@Valid @RequestBody SyncRequest request, Authentication authentication) {
        return syncService.process(authentication.getName(), request);
    }

    @GetMapping("/sync-policy")
    public SyncPolicyDto getPolicy() {
        return syncPolicyService.get();
    }

    @PutMapping("/sync-policy")
    public SyncPolicyDto updatePolicy(@RequestBody SyncPolicyDto dto) {
        return syncPolicyService.update(dto);
    }

    /** Admin sync monitor. */
    @GetMapping("/admin/sync-logs")
    public List<SyncLogDto> logs() {
        return syncService.allLogs();
    }
}
