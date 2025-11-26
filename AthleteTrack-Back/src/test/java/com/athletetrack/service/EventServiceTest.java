package com.athletetrack.service;

import com.athletetrack.dto.EventDto;
import com.athletetrack.entity.Event;
import com.athletetrack.entity.EventRegistration;
import com.athletetrack.entity.User;
import com.athletetrack.repository.EventRegistrationRepository;
import com.athletetrack.repository.EventRepository;
import com.athletetrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Unit Tests")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventRegistrationRepository registrationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private User testUser;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Marathon 2024");
        testEvent.setDate(LocalDate.of(2024, 6, 15));
        testEvent.setDateDisplay("15 Jun 2024");
        testEvent.setLocation("Madrid");
        testEvent.setParticipants(10);
        testEvent.setMaxParticipants(100);
        testEvent.setImage("https://example.com/marathon.jpg");
        testEvent.setCategory("Running");
        testEvent.setDistance("42km");
        testEvent.setDifficulty("Hard");
        testEvent.setDescription("Annual marathon event");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setUsername("testuser");
    }

    // ========== GET ALL EVENTS TESTS ==========

    @Test
    @DisplayName("Should get all events ordered by date ascending")
    void shouldGetAllEventsOrderedByDate() {
        // Given
        Event event1 = new Event();
        event1.setId(1L);
        event1.setDate(LocalDate.of(2024, 6, 15));

        Event event2 = new Event();
        event2.setId(2L);
        event2.setDate(LocalDate.of(2024, 5, 10));

        when(eventRepository.findAllByOrderByDateAsc()).thenReturn(Arrays.asList(event2, event1));

        // When
        List<EventDto> result = eventService.getAllEvents();

        // Then
        assertThat(result).hasSize(2);
        verify(eventRepository).findAllByOrderByDateAsc();
    }

    // ========== GET EVENT BY ID TESTS ==========

    @Test
    @DisplayName("Should get event by ID successfully")
    void shouldGetEventByIdSuccessfully() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When
        EventDto result = eventService.getEventById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Marathon 2024");
        verify(eventRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return null when event not found")
    void shouldReturnNullWhenEventNotFound() {
        // Given
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        EventDto result = eventService.getEventById(999L);

        // Then
        assertThat(result).isNull();
        verify(eventRepository).findById(999L);
    }

    // ========== CREATE EVENT TESTS ==========

    @Test
    @DisplayName("Should create event with all fields")
    void shouldCreateEventWithAllFields() {
        // Given
        EventDto dto = new EventDto();
        dto.setTitle("New Marathon");
        dto.setDate(LocalDate.of(2024, 7, 20));
        dto.setDateDisplay("20 Jul 2024");
        dto.setLocation("Barcelona");
        dto.setMaxParticipants(150);
        dto.setImage("https://example.com/event.jpg");
        dto.setCategory("Running");
        dto.setDistance("21km");
        dto.setDifficulty("Medium");
        dto.setDescription("Half marathon event");
        dto.setParticipants(0);

        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        EventDto result = eventService.createEvent(dto);

        // Then
        assertThat(result).isNotNull();
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("Should set default maxParticipants when not provided")
    void shouldSetDefaultMaxParticipants() {
        // Given
        EventDto dto = new EventDto();
        dto.setTitle("Event");
        dto.setDate(LocalDate.now());
        dto.setLocation("Location");
        dto.setMaxParticipants(null); // Not provided

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            assertThat(saved.getMaxParticipants()).isEqualTo(100); // Default value
            return saved;
        });

        // When
        eventService.createEvent(dto);

        // Then
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("Should set default participants to 0 when not provided")
    void shouldSetDefaultParticipants() {
        // Given
        EventDto dto = new EventDto();
        dto.setTitle("Event");
        dto.setDate(LocalDate.now());
        dto.setLocation("Location");
        dto.setParticipants(null);

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            assertThat(saved.getParticipants()).isEqualTo(0);
            return saved;
        });

        // When
        eventService.createEvent(dto);

        // Then
        verify(eventRepository).save(any(Event.class));
    }

    // ========== UPDATE EVENT TESTS ==========

    @Test
    @DisplayName("Should update event with partial fields")
    void shouldUpdateEventPartialFields() {
        // Given
        EventDto dto = new EventDto();
        dto.setTitle("Updated Title");
        dto.setLocation("Updated Location");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        EventDto result = eventService.updateEvent(1L, dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(testEvent.getTitle()).isEqualTo("Updated Title");
        assertThat(testEvent.getLocation()).isEqualTo("Updated Location");
        verify(eventRepository).save(testEvent);
    }

    @Test
    @DisplayName("Should return null when updating non-existent event")
    void shouldReturnNullWhenUpdatingNonExistent() {
        // Given
        EventDto dto = new EventDto();
        dto.setTitle("New Title");

        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        EventDto result = eventService.updateEvent(999L, dto);

        // Then
        assertThat(result).isNull();
        verify(eventRepository, never()).save(any(Event.class));
    }

    // ========== DELETE EVENT TESTS ==========

    @Test
    @DisplayName("Should delete event successfully")
    void shouldDeleteEventSuccessfully() {
        // Given
        when(eventRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(1L);

        // When
        boolean result = eventService.deleteEvent(1L);

        // Then
        assertThat(result).isTrue();
        verify(eventRepository).existsById(1L);
        verify(eventRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent event")
    void shouldReturnFalseWhenDeletingNonExistent() {
        // Given
        when(eventRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = eventService.deleteEvent(999L);

        // Then
        assertThat(result).isFalse();
        verify(eventRepository, never()).deleteById(anyLong());
    }

    // ========== USER REGISTRATION TESTS ==========

    @Test
    @DisplayName("Should register user to event successfully")
    void shouldRegisterUserToEventSuccessfully() {
        // Given
        testEvent.setParticipants(5);

        when(registrationRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(registrationRepository.save(any(EventRegistration.class))).thenReturn(new EventRegistration());
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        boolean result = eventService.registerUserToEvent(1L, 1L);

        // Then
        assertThat(result).isTrue();
        assertThat(testEvent.getParticipants()).isEqualTo(6);
        verify(registrationRepository).save(any(EventRegistration.class));
        verify(eventRepository).save(testEvent);
    }

    @Test
    @DisplayName("Should increment participants from null to 1")
    void shouldIncrementFromNullToOne() {
        // Given
        testEvent.setParticipants(null);

        when(registrationRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(registrationRepository.save(any(EventRegistration.class))).thenReturn(new EventRegistration());
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        eventService.registerUserToEvent(1L, 1L);

        // Then
        assertThat(testEvent.getParticipants()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return false when user already registered")
    void shouldReturnFalseWhenAlreadyRegistered() {
        // Given
        when(registrationRepository.findByUserIdAndEventId(1L, 1L))
                .thenReturn(Optional.of(new EventRegistration()));

        // When
        boolean result = eventService.registerUserToEvent(1L, 1L);

        // Then
        assertThat(result).isFalse();
        verify(registrationRepository, never()).save(any(EventRegistration.class));
    }

    @Test
    @DisplayName("Should return false when user not found")
    void shouldReturnFalseWhenUserNotFound() {
        // Given
        when(registrationRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = eventService.registerUserToEvent(1L, 1L);

        // Then
        assertThat(result).isFalse();
        verify(registrationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return false when event not found")
    void shouldReturnFalseWhenEventNotFound() {
        // Given
        when(registrationRepository.findByUserIdAndEventId(1L, 999L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = eventService.registerUserToEvent(999L, 1L);

        // Then
        assertThat(result).isFalse();
        verify(registrationRepository, never()).save(any());
    }

    // ========== UNREGISTER TESTS ==========

    @Test
    @DisplayName("Should unregister user from event successfully")
    void shouldUnregisterUserSuccessfully() {
        // Given
        EventRegistration registration = new EventRegistration();
        testEvent.setParticipants(10);

        when(registrationRepository.findByUserIdAndEventId(1L, 1L))
                .thenReturn(Optional.of(registration));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        doNothing().when(registrationRepository).delete(registration);
        when(eventRepository.save(testEvent)).thenReturn(testEvent);

        // When
        boolean result = eventService.unregisterUserFromEvent(1L, 1L);

        // Then
        assertThat(result).isTrue();
        assertThat(testEvent.getParticipants()).isEqualTo(9);
        verify(registrationRepository).delete(registration);
        verify(eventRepository).save(testEvent);
    }

    @Test
    @DisplayName("Should not decrement below zero")
    void shouldNotDecrementBelowZero() {
        // Given
        EventRegistration registration = new EventRegistration();
        testEvent.setParticipants(0);

        when(registrationRepository.findByUserIdAndEventId(1L, 1L))
                .thenReturn(Optional.of(registration));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When
        eventService.unregisterUserFromEvent(1L, 1L);

        // Then
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Should return false when registration not found")
    void shouldReturnFalseWhenRegistrationNotFound() {
        // Given
        when(registrationRepository.findByUserIdAndEventId(1L, 1L))
                .thenReturn(Optional.empty());

        // When
        boolean result = eventService.unregisterUserFromEvent(1L, 1L);

        // Then
        assertThat(result).isFalse();
        verify(registrationRepository, never()).delete(any());
    }

    // ========== FIND REGISTERED EVENTS TESTS ==========

    @Test
    @DisplayName("Should find registered event IDs by user")
    void shouldFindRegisteredEventIdsByUser() {
        // Given
        List<Long> eventIds = Arrays.asList(1L, 2L, 3L);
        when(registrationRepository.findEventIdsByUserId(1L)).thenReturn(eventIds);

        // When
        List<Long> result = eventService.findRegisteredEventIdsByUser(1L);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(1L, 2L, 3L);
        verify(registrationRepository).findEventIdsByUserId(1L);
    }

    // ========== TO DTO TESTS ==========

    @Test
    @DisplayName("Should convert Event to EventDto correctly")
    void shouldConvertEventToDto() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When
        EventDto result = eventService.getEventById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testEvent.getId());
        assertThat(result.getTitle()).isEqualTo(testEvent.getTitle());
        assertThat(result.getDate()).isEqualTo(testEvent.getDate());
        assertThat(result.getDateDisplay()).isEqualTo(testEvent.getDateDisplay());
        assertThat(result.getLocation()).isEqualTo(testEvent.getLocation());
        assertThat(result.getParticipants()).isEqualTo(testEvent.getParticipants());
        assertThat(result.getMaxParticipants()).isEqualTo(testEvent.getMaxParticipants());
        assertThat(result.getImage()).isEqualTo(testEvent.getImage());
        assertThat(result.getCategory()).isEqualTo(testEvent.getCategory());
        assertThat(result.getDistance()).isEqualTo(testEvent.getDistance());
        assertThat(result.getDifficulty()).isEqualTo(testEvent.getDifficulty());
        assertThat(result.getDescription()).isEqualTo(testEvent.getDescription());
    }
}
