package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.SubmissionDto;
import com.it342g02.vibecoder.edureach.service.SubmissionService;
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
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping
    public List<SubmissionDto> getAll(@RequestParam(required = false) Long assessmentId, @RequestParam(required = false) Long userId) {
        return submissionService.findAll(assessmentId, userId);
    }

    @GetMapping("/{id}")
    public SubmissionDto getById(@PathVariable Long id) {
        return submissionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionDto create(@Valid @RequestBody SubmissionDto dto) {
        return submissionService.create(dto);
    }

    @PutMapping("/{id}")
    public SubmissionDto update(@PathVariable Long id, @Valid @RequestBody SubmissionDto dto) {
        return submissionService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        submissionService.delete(id);
    }
}
