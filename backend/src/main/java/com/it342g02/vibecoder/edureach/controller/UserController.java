package com.it342g02.vibecoder.edureach.controller;

import com.it342g02.vibecoder.edureach.dto.RoleChangeRequest;
import com.it342g02.vibecoder.edureach.dto.UserDto;
import com.it342g02.vibecoder.edureach.entity.Role;
import com.it342g02.vibecoder.edureach.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam(required = false) Role role) {
        return userService.findAll(role);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UserDto dto, Authentication authentication) {
        return userService.update(id, dto, authentication);
    }

    @PutMapping("/{id}/role")
    public UserDto changeRole(@PathVariable Long id, @Valid @RequestBody RoleChangeRequest request) {
        return userService.changeRole(id, request.role());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
