package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.SubmissionAnswerDto;
import com.it342g02.vibecoder.edureach.service.SubmissionAnswerService;
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
@RequestMapping("/api/submission-answers")
public class SubmissionAnswerController {

    private final SubmissionAnswerService submissionAnswerService;

    public SubmissionAnswerController(SubmissionAnswerService submissionAnswerService) {
        this.submissionAnswerService = submissionAnswerService;
    }

    @GetMapping
    public List<SubmissionAnswerDto> getAll(@RequestParam(required = false) Long submissionId) {
        return submissionAnswerService.findAll(submissionId);
    }

    @GetMapping("/{id}")
    public SubmissionAnswerDto getById(@PathVariable Long id) {
        return submissionAnswerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionAnswerDto create(@Valid @RequestBody SubmissionAnswerDto dto) {
        return submissionAnswerService.create(dto);
    }

    @PutMapping("/{id}")
    public SubmissionAnswerDto update(@PathVariable Long id, @Valid @RequestBody SubmissionAnswerDto dto) {
        return submissionAnswerService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        submissionAnswerService.delete(id);
    }
}
