package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.OfflinePackageDto;
import com.it342g02.vibecoder.edureach.service.OfflineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offline")
public class OfflineController {

    private final OfflineService offlineService;

    public OfflineController(OfflineService offlineService) {
        this.offlineService = offlineService;
    }

    @GetMapping("/courses/{courseId}/package")
    public OfflinePackageDto coursePackage(@PathVariable Long courseId) {
        return offlineService.buildPackage(courseId);
    }
}
