package com.athletetrack.controller;

import com.athletetrack.dto.WorkoutDto;
import com.athletetrack.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping("/{userId}")
    public List<WorkoutDto> getWorkouts(@PathVariable Long userId) {
        return workoutService.getWorkoutsByUser(userId);
    }

    @PostMapping
    public ResponseEntity<WorkoutDto> createWorkout(@RequestBody WorkoutDto dto, org.springframework.security.core.Authentication authentication) {
        // Prefer authenticated user over client-provided userId
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.athletetrack.entity.User user) {
            dto.setUserId(user.getId());
        }
        WorkoutDto created = workoutService.createWorkout(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        boolean ok = workoutService.deleteWorkout(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
