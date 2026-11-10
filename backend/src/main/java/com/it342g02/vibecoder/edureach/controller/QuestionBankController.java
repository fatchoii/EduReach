package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.QuestionBankDto;
import com.it342g02.vibecoder.edureach.service.QuestionBankService;
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
@RequestMapping("/api/questions")
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    public QuestionBankController(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    @GetMapping
    public List<QuestionBankDto> getAll(@RequestParam(required = false) Long assessmentId, @RequestParam(required = false) Long moduleId) {
        return questionBankService.findAll(assessmentId, moduleId);
    }

    @GetMapping("/{id}")
    public QuestionBankDto getById(@PathVariable Long id) {
        return questionBankService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionBankDto create(@Valid @RequestBody QuestionBankDto dto) {
        return questionBankService.create(dto);
    }

    @PutMapping("/{id}")
    public QuestionBankDto update(@PathVariable Long id, @Valid @RequestBody QuestionBankDto dto) {
        return questionBankService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        questionBankService.delete(id);
    }
}
