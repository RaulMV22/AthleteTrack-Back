package com.athletetrack.controller;

import com.athletetrack.dto.EventDto;
import com.athletetrack.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventDto> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id) {
        EventDto dto = eventService.getEventById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventDto dto) {
        EventDto created = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @Valid @RequestBody EventDto dto) {
        EventDto updated = eventService.updateEvent(id, dto);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        boolean deleted = eventService.deleteEvent(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/registrations/{userId}")
    public ResponseEntity<List<Long>> getUserRegistrations(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.findRegisteredEventIdsByUser(userId));
    }

    @PostMapping("/{eventId}/register")
    public ResponseEntity<Void> registerForEvent(@PathVariable Long eventId, org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof com.athletetrack.entity.User user)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Long userId = user.getId();
        boolean ok = eventService.registerUserToEvent(eventId, userId);
        if (!ok) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{eventId}/register")
    public ResponseEntity<Void> unregisterFromEvent(@PathVariable Long eventId, org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof com.athletetrack.entity.User user)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Long userId = user.getId();
        boolean ok = eventService.unregisterUserFromEvent(eventId, userId);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
