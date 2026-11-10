package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.AssessmentDto;
import com.it342g02.vibecoder.edureach.service.AssessmentService;
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
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping
    public List<AssessmentDto> getAll(@RequestParam(required = false) Long moduleId) {
        return assessmentService.findAll(moduleId);
    }

    @GetMapping("/{id}")
    public AssessmentDto getById(@PathVariable Long id) {
        return assessmentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentDto create(@Valid @RequestBody AssessmentDto dto) {
        return assessmentService.create(dto);
    }

    @PutMapping("/{id}")
    public AssessmentDto update(@PathVariable Long id, @Valid @RequestBody AssessmentDto dto) {
        return assessmentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        assessmentService.delete(id);
    }
}
