package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.ParentStudentDto;
import com.it342g02.vibecoder.edureach.service.ParentStudentService;
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
@RequestMapping("/api/parent-students")
public class ParentStudentController {

    private final ParentStudentService parentStudentService;

    public ParentStudentController(ParentStudentService parentStudentService) {
        this.parentStudentService = parentStudentService;
    }

    @GetMapping
    public List<ParentStudentDto> getAll(@RequestParam(required = false) Long parentId, @RequestParam(required = false) Long studentId) {
        return parentStudentService.findAll(parentId, studentId);
    }

    @GetMapping("/{id}")
    public ParentStudentDto getById(@PathVariable Long id) {
        return parentStudentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParentStudentDto create(@Valid @RequestBody ParentStudentDto dto) {
        return parentStudentService.create(dto);
    }

    @PutMapping("/{id}")
    public ParentStudentDto update(@PathVariable Long id, @Valid @RequestBody ParentStudentDto dto) {
        return parentStudentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        parentStudentService.delete(id);
    }
}
