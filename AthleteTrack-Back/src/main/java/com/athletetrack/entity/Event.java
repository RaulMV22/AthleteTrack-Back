package com.athletetrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(length = 100)
    private String dateDisplay;
    
    @Column(nullable = false)
    private String location;
    
    private Integer participants = 0;
    
    @Column(nullable = false)
    private Integer maxParticipants = 100;
    
    private String image;
    
    @Column(length = 100)
    private String category;
    
    @Column(length = 100)
    private String distance;
    
    @Column(length = 100)
    private String difficulty;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventRegistration> registrations = new HashSet<>();
}
