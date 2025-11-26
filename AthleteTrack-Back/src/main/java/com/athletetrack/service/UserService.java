package com.athletetrack.service;

import com.athletetrack.dto.UpdateProfileRequest;
import com.athletetrack.dto.UserDto;
import com.athletetrack.entity.User;
import com.athletetrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Updates the profile information of a user.
     * 
     * @param userId  The ID of the user to update
     * @param request The profile update data
     * @return Updated user DTO
     * @throws RuntimeException if user not found or email already taken
     */
    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Update name if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }

        // Update email if provided and not already taken by another user
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equals(user.getEmail().toLowerCase())) {
                // Check if email is already taken
                if (userRepository.existsByEmail(newEmail)) {
                    throw new RuntimeException("El email ya está en uso");
                }
                user.setEmail(newEmail);
            }
        }

        // Update avatar if provided
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar().trim());
        }

        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    /**
     * Converts User entity to UserDto
     */
    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setAvatar(user.getAvatar());
        return dto;
    }
}
