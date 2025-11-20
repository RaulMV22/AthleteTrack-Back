package com.athletetrack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventDto {
    private Long id;
    @NotBlank(message = "title is required")
    private String title;
    private LocalDate date;
    private String dateDisplay;
    private String location;
    private Integer participants;
    private Integer maxParticipants;
    private String image;
    private String category;
    private String distance;
    private String difficulty;
    private String description;
}
