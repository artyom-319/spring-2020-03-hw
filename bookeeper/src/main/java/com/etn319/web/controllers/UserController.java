package com.etn319.web.controllers;

import com.etn319.service.common.api.UserService;
import com.etn319.web.dto.UserDto;
import com.etn319.web.dto.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("/api/me")
    public ResponseEntity<UserDto> getCurrentUserInfo() {
        return service.loadCurrentAuthenticatedUser()
                .map(UserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }
}
