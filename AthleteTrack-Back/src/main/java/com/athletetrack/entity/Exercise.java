package com.athletetrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;
    
    @Column(nullable = false)
    private String exercise;
    
    @Column(length = 50)
    private String sets;
    
    @Column(length = 50)
    private String reps;
    
    @Column(length = 50)
    private String weight;
    
    @Column(length = 10)
    private String weightUnit = "kg";
    
    @Column(length = 50)
    private String time;
}
