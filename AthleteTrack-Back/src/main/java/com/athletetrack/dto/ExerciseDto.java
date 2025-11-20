package com.athletetrack.dto;

import lombok.Data;

@Data
public class ExerciseDto {
    private Long id;
    private String exercise;
    private String sets;
    private String reps;
    private String weight;
    private String weightUnit;
    private String time;
}
