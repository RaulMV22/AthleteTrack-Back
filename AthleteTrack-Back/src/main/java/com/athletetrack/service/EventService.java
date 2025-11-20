
package com.athletetrack.service;

import com.athletetrack.dto.EventDto;
import com.athletetrack.entity.Event;
import com.athletetrack.entity.EventRegistration;
import com.athletetrack.entity.User;
import com.athletetrack.repository.EventRegistrationRepository;
import com.athletetrack.repository.EventRepository;
import com.athletetrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public List<EventDto> getAllEvents() {
        // Ordenados por fecha ascendente
        return eventRepository.findAllByOrderByDateAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public EventDto getEventById(Long id) {
        return eventRepository.findById(id).map(this::toDto).orElse(null);
    }

    public EventDto createEvent(EventDto dto) {
        Event e = new Event();
        e.setTitle(dto.getTitle());
        e.setDate(dto.getDate());
        e.setDateDisplay(dto.getDateDisplay());
        e.setLocation(dto.getLocation());
        e.setMaxParticipants(dto.getMaxParticipants() != null ? dto.getMaxParticipants() : 100);
        e.setImage(dto.getImage());
        e.setCategory(dto.getCategory());
        e.setDistance(dto.getDistance());
        e.setDifficulty(dto.getDifficulty());
        e.setDescription(dto.getDescription());
        e.setParticipants(dto.getParticipants() != null ? dto.getParticipants() : 0);
        Event saved = eventRepository.save(e);
        return toDto(saved);
    }

    public EventDto updateEvent(Long id, EventDto dto) {
        return eventRepository.findById(id).map(e -> {
            if (dto.getTitle() != null) e.setTitle(dto.getTitle());
            if (dto.getDate() != null) e.setDate(dto.getDate());
            if (dto.getDateDisplay() != null) e.setDateDisplay(dto.getDateDisplay());
            if (dto.getLocation() != null) e.setLocation(dto.getLocation());
            if (dto.getMaxParticipants() != null) e.setMaxParticipants(dto.getMaxParticipants());
            if (dto.getImage() != null) e.setImage(dto.getImage());
            if (dto.getCategory() != null) e.setCategory(dto.getCategory());
            if (dto.getDistance() != null) e.setDistance(dto.getDistance());
            if (dto.getDifficulty() != null) e.setDifficulty(dto.getDifficulty());
            if (dto.getDescription() != null) e.setDescription(dto.getDescription());
            Event saved = eventRepository.save(e);
            return toDto(saved);
        }).orElse(null);
    }

    public boolean deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) return false;
        eventRepository.deleteById(id);
        return true;
    }

    public List<Long> findRegisteredEventIdsByUser(Long userId) {
        return registrationRepository.findEventIdsByUserId(userId);
    }

    public boolean registerUserToEvent(Long eventId, Long userId) {
        if (registrationRepository.findByUserIdAndEventId(userId, eventId).isPresent()) return false;
        User user = userRepository.findById(userId).orElse(null);
        Event event = eventRepository.findById(eventId).orElse(null);
        if (user == null || event == null) return false;
        EventRegistration reg = new EventRegistration();
        reg.setEvent(event);
        reg.setUser(user);
        registrationRepository.save(reg);
        event.setParticipants(event.getParticipants() == null ? 1 : event.getParticipants() + 1);
        eventRepository.save(event);
        return true;
    }

    public boolean unregisterUserFromEvent(Long eventId, Long userId) {
        var opt = registrationRepository.findByUserIdAndEventId(userId, eventId);
        if (opt.isEmpty()) return false;
        EventRegistration reg = opt.get();
        registrationRepository.delete(reg);
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event != null && event.getParticipants() != null && event.getParticipants() > 0) {
            event.setParticipants(event.getParticipants() - 1);
            eventRepository.save(event);
        }
        return true;
    }

    private EventDto toDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDate(event.getDate());
        dto.setDateDisplay(event.getDateDisplay());
        dto.setLocation(event.getLocation());
        dto.setParticipants(event.getParticipants());
        dto.setMaxParticipants(event.getMaxParticipants());
        dto.setImage(event.getImage());
        dto.setCategory(event.getCategory());
        dto.setDistance(event.getDistance());
        dto.setDifficulty(event.getDifficulty());
        dto.setDescription(event.getDescription());
        return dto;
    }
}
