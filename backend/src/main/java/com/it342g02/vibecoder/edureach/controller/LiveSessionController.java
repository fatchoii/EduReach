package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.LiveSessionDto;
import com.it342g02.vibecoder.edureach.service.LiveSessionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/live-sessions")
public class LiveSessionController {

    private final LiveSessionService liveSessionService;

    public LiveSessionController(LiveSessionService liveSessionService) {
        this.liveSessionService = liveSessionService;
    }

    @GetMapping
    public List<LiveSessionDto> getAll(@RequestParam(required = false) Long courseId) {
        return liveSessionService.findAll(courseId);
    }

    @GetMapping("/{id}")
    public LiveSessionDto getById(@PathVariable Long id) {
        return liveSessionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LiveSessionDto create(@Valid @RequestBody LiveSessionDto dto) {
        return liveSessionService.create(dto);
    }

    @PutMapping("/{id}")
    public LiveSessionDto update(@PathVariable Long id, @Valid @RequestBody LiveSessionDto dto) {
        return liveSessionService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        liveSessionService.delete(id);
    }
}
