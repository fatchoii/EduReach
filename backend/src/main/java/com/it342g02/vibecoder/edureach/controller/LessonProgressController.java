package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.LessonProgressDto;
import com.it342g02.vibecoder.edureach.service.LessonProgressService;
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
@RequestMapping("/api/lesson-progress")
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;

    public LessonProgressController(LessonProgressService lessonProgressService) {
        this.lessonProgressService = lessonProgressService;
    }

    @GetMapping
    public List<LessonProgressDto> getAll(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long lessonId) {
        return lessonProgressService.findAll(userId, lessonId);
    }

    @GetMapping("/{id}")
    public LessonProgressDto getById(@PathVariable Long id) {
        return lessonProgressService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonProgressDto create(@Valid @RequestBody LessonProgressDto dto) {
        return lessonProgressService.create(dto);
    }

    @PutMapping("/{id}")
    public LessonProgressDto update(@PathVariable Long id, @Valid @RequestBody LessonProgressDto dto) {
        return lessonProgressService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        lessonProgressService.delete(id);
    }
}
