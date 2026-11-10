package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.UserBadgeDto;
import com.it342g02.vibecoder.edureach.service.UserBadgeService;
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
@RequestMapping("/api/badges")
public class UserBadgeController {

    private final UserBadgeService userBadgeService;

    public UserBadgeController(UserBadgeService userBadgeService) {
        this.userBadgeService = userBadgeService;
    }

    @GetMapping
    public List<UserBadgeDto> getAll(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long courseId) {
        return userBadgeService.findAll(userId, courseId);
    }

    @GetMapping("/{id}")
    public UserBadgeDto getById(@PathVariable Long id) {
        return userBadgeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserBadgeDto create(@Valid @RequestBody UserBadgeDto dto) {
        return userBadgeService.create(dto);
    }

    @PutMapping("/{id}")
    public UserBadgeDto update(@PathVariable Long id, @Valid @RequestBody UserBadgeDto dto) {
        return userBadgeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userBadgeService.delete(id);
    }
}
