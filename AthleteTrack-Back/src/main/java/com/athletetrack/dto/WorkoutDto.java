package com.athletetrack.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WorkoutDto {
    private Long id;
    private Long userId;
    private LocalDate date;
    private String notes;
    private List<ExerciseDto> exercises;
}
