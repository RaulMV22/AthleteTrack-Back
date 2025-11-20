package com.athletetrack.service;

import com.athletetrack.dto.ExerciseDto;
import com.athletetrack.dto.WorkoutDto;
import com.athletetrack.entity.Exercise;
import com.athletetrack.entity.User;
import com.athletetrack.entity.Workout;
import com.athletetrack.repository.ExerciseRepository;
import com.athletetrack.repository.UserRepository;
import com.athletetrack.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public List<WorkoutDto> getWorkoutsByUser(Long userId) {
        return workoutRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public WorkoutDto createWorkout(WorkoutDto dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Workout w = new Workout();
        w.setUser(user);
        w.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());
        w.setNotes(dto.getNotes());
        Workout saved = workoutRepository.save(w);

        if (dto.getExercises() != null) {
            List<Exercise> exercises = dto.getExercises().stream().map(ed -> {
                Exercise e = new Exercise();
                e.setWorkout(saved);
                e.setExercise(ed.getExercise());
                e.setSets(ed.getSets());
                e.setReps(ed.getReps());
                e.setWeight(ed.getWeight());
                e.setWeightUnit(ed.getWeightUnit());
                e.setTime(ed.getTime());
                return e;
            }).collect(Collectors.toList());
            exerciseRepository.saveAll(exercises);
            saved.setExercises(exercises);
        }

        return toDto(saved);
    }

    public boolean deleteWorkout(Long id) {
        if (!workoutRepository.existsById(id)) return false;
        workoutRepository.deleteById(id);
        return true;
    }

    private WorkoutDto toDto(Workout w) {
        WorkoutDto dto = new WorkoutDto();
        dto.setId(w.getId());
        dto.setUserId(w.getUser() != null ? w.getUser().getId() : null);
        dto.setDate(w.getDate());
        dto.setNotes(w.getNotes());
        if (w.getExercises() != null) {
            dto.setExercises(w.getExercises().stream().map(e -> {
                ExerciseDto ed = new ExerciseDto();
                ed.setId(e.getId());
                ed.setExercise(e.getExercise());
                ed.setSets(e.getSets());
                ed.setReps(e.getReps());
                ed.setWeight(e.getWeight());
                ed.setWeightUnit(e.getWeightUnit());
                ed.setTime(e.getTime());
                return ed;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
