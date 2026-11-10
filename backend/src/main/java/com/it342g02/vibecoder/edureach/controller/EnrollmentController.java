package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.EnrollmentDto;
import com.it342g02.vibecoder.edureach.service.EnrollmentService;
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
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public List<EnrollmentDto> getAll(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long courseId) {
        return enrollmentService.findAll(userId, courseId);
    }

    @GetMapping("/{id}")
    public EnrollmentDto getById(@PathVariable Long id) {
        return enrollmentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentDto create(@Valid @RequestBody EnrollmentDto dto) {
        return enrollmentService.create(dto);
    }

    @PutMapping("/{id}")
    public EnrollmentDto update(@PathVariable Long id, @Valid @RequestBody EnrollmentDto dto) {
        return enrollmentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        enrollmentService.delete(id);
    }
}
