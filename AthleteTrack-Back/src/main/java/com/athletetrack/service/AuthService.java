package com.athletetrack.service;

import com.athletetrack.dto.AuthResponse;
import com.athletetrack.dto.RegisterRequest;
import com.athletetrack.dto.LoginRequest;
import com.athletetrack.dto.UserDto;
import com.athletetrack.entity.User;
import com.athletetrack.repository.UserRepository;
import com.athletetrack.Security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()) || userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Usuario ya existe");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        UserDto dto = toDto(user);
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(dto, token);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        UserDto dto = toDto(user);
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(dto, token);
    }

    public UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setAvatar(user.getAvatar());
        return dto;
    }

    public AuthResponse googleAuth(String idToken) {
        try {
            // Decode Google ID token (format: header.payload.signature)
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("Token inválido");
            }

            // Decode payload (add padding if needed)
            String payload = parts[1];
            int padding = 4 - (payload.length() % 4);
            if (padding != 4) {
                payload += "=".repeat(padding);
            }

            byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            String decodedPayload = new String(decodedBytes);

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenPayload = mapper.readTree(decodedPayload);

            String email = tokenPayload.get("email").asText();
            String name = tokenPayload.get("name").asText();
            String picture = tokenPayload.has("picture") ? tokenPayload.get("picture").asText() : null;

            // Check if user exists
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                // Create new user from Google data
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                // Generate username from email
                String username = email.split("@")[0];
                int counter = 1;
                while (userRepository.existsByUsername(username)) {
                    username = email.split("@")[0] + counter++;
                }
                newUser.setUsername(username);
                // Set a random password (user won't use it)
                newUser.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                newUser.setAvatar(picture);
                return userRepository.save(newUser);
            });

            UserDto dto = toDto(user);
            String token = jwtUtil.generateToken(user);
            return new AuthResponse(dto, token);

        } catch (Exception e) {
            throw new RuntimeException("Error procesando Google token: " + e.getMessage());
        }
    }
}
