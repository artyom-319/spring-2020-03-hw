package com.etn319.web.controllers;

import com.etn319.model.ServiceUser;
import com.etn319.security.Roles;
import com.etn319.service.common.api.UserService;
import com.etn319.web.dto.UserDto;
import com.etn319.web.dto.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final UserService service;

    @GetMapping("/api/adminka/users")
    public ResponseEntity<List<UserDto>> loadUsers() {
        List<UserDto> users = service.getAllWithRoles().stream()
                .map(UserMapper::toDto)
                .collect(toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/api/adminka/roles")
    public ResponseEntity<List<String>> loadRoles() {
        return ResponseEntity.ok(Roles.all());
    }

    @PostMapping("/api/adminka/users")
    public ResponseEntity<List<UserDto>> updateUserRoles(@RequestBody List<UserDto> userDtos) {
        List<ServiceUser> users = userDtos.stream().map(UserMapper::toDomainObject).collect(toList());
        List<UserDto> updatedDtos = service.updateUserRoles(users).stream().map(UserMapper::toDto).collect(toList());
        return ResponseEntity.ok(updatedDtos);
    }
}
