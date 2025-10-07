package com.project.approval.controller;

import com.project.approval.dto.UserWithPositionDTO;
import org.springframework.web.bind.annotation.*;
import com.project.approval.service.UserServiceInter;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceInter service;

    public UserController(UserServiceInter service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public UserWithPositionDTO getUser(@PathVariable Long id) {
        return service.getUserWithPosition(id);
    }

    @GetMapping
    public List<UserWithPositionDTO> getAllUsers() {
        return service.getAllUsersWithPosition();
    }
}