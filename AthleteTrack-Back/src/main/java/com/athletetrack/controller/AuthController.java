package com.athletetrack.controller;

import com.athletetrack.dto.AuthResponse;
import com.athletetrack.dto.GoogleAuthRequest;
import com.athletetrack.dto.LoginRequest;
import com.athletetrack.dto.RegisterRequest;
import com.athletetrack.dto.UserDto;
import com.athletetrack.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse resp = authService.register(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> google(@Valid @RequestBody GoogleAuthRequest req) {
        AuthResponse resp = authService.googleAuth(req.getToken());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.athletetrack.entity.User user) {
            UserDto dto = authService.toDto(user);
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(401).build();
    }
}
