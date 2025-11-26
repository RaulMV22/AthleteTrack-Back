package com.athletetrack.service;

import com.athletetrack.dto.ExerciseDto;
import com.athletetrack.dto.WorkoutDto;
import com.athletetrack.entity.Exercise;
import com.athletetrack.entity.User;
import com.athletetrack.entity.Workout;
import com.athletetrack.repository.ExerciseRepository;
import com.athletetrack.repository.UserRepository;
import com.athletetrack.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkoutService Unit Tests")
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private WorkoutService workoutService;

    private User testUser;
    private Workout testWorkout;
    private Exercise testExercise;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setUsername("testuser");

        testWorkout = new Workout();
        testWorkout.setId(1L);
        testWorkout.setUser(testUser);
        testWorkout.setDate(LocalDate.now());
        testWorkout.setNotes("Test workout notes");
        testWorkout.setExercises(new ArrayList<>());

        testExercise = new Exercise();
        testExercise.setId(1L);
        testExercise.setWorkout(testWorkout);
        testExercise.setExercise("Bench Press");
        testExercise.setSets("3");
        testExercise.setReps("10");
        testExercise.setWeight("80.0");
        testExercise.setWeightUnit("kg");
    }

    // ========== GET WORKOUTS BY USER TESTS ==========

    @Test
    @DisplayName("Should get workouts by user ordered by date descending")
    void shouldGetWorkoutsByUserOrderedByDate() {
        // Given
        Workout workout1 = new Workout();
        workout1.setId(1L);
        workout1.setDate(LocalDate.of(2024, 6, 15));

        Workout workout2 = new Workout();
        workout2.setId(2L);
        workout2.setDate(LocalDate.of(2024, 6, 20));

        when(workoutRepository.findByUserIdOrderByDateDesc(1L))
                .thenReturn(Arrays.asList(workout2, workout1));

        // When
        List<WorkoutDto> result = workoutService.getWorkoutsByUser(1L);

        // Then
        assertThat(result).hasSize(2);
        verify(workoutRepository).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    @DisplayName("Should return empty list when user has no workouts")
    void shouldReturnEmptyListWhenNoWorkouts() {
        // Given
        when(workoutRepository.findByUserIdOrderByDateDesc(1L))
                .thenReturn(new ArrayList<>());

        // When
        List<WorkoutDto> result = workoutService.getWorkoutsByUser(1L);

        // Then
        assertThat(result).isEmpty();
        verify(workoutRepository).findByUserIdOrderByDateDesc(1L);
    }

    // ========== CREATE WORKOUT TESTS ==========

    @Test
    @DisplayName("Should create workout with exercises")
    void shouldCreateWorkoutWithExercises() {
        // Given
        WorkoutDto dto = new WorkoutDto();
        dto.setUserId(1L);
        dto.setDate(LocalDate.of(2024, 6, 15));
        dto.setNotes("Chest day");

        ExerciseDto exerciseDto = new ExerciseDto();
        exerciseDto.setExercise("Bench Press");
        exerciseDto.setSets("3");
        exerciseDto.setReps("10");
        exerciseDto.setWeight("80.0");
        exerciseDto.setWeightUnit("kg");

        dto.setExercises(Arrays.asList(exerciseDto));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.save(any(Workout.class))).thenReturn(testWorkout);
        when(exerciseRepository.saveAll(anyList())).thenReturn(Arrays.asList(testExercise));

        // When
        WorkoutDto result = workoutService.createWorkout(dto);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(workoutRepository).save(any(Workout.class));
        verify(exerciseRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Should create workout without exercises")
    void shouldCreateWorkoutWithoutExercises() {
        // Given
        WorkoutDto dto = new WorkoutDto();
        dto.setUserId(1L);
        dto.setDate(LocalDate.of(2024, 6, 15));
        dto.setNotes("Rest day");
        dto.setExercises(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.save(any(Workout.class))).thenReturn(testWorkout);

        // When
        WorkoutDto result = workoutService.createWorkout(dto);

        // Then
        assertThat(result).isNotNull();
        verify(exerciseRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        WorkoutDto dto = new WorkoutDto();
        dto.setUserId(999L);
        dto.setDate(LocalDate.now());

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> workoutService.createWorkout(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        verify(workoutRepository, never()).save(any(Workout.class));
    }

    @Test
    @DisplayName("Should use current date when date not provided")
    void shouldUseCurrentDateWhenNotProvided() {
        // Given
        WorkoutDto dto = new WorkoutDto();
        dto.setUserId(1L);
        dto.setDate(null);

        LocalDate today = LocalDate.now();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> {
            Workout saved = invocation.getArgument(0);
            assertThat(saved.getDate()).isEqualTo(today);
            return saved;
        });

        // When
        workoutService.createWorkout(dto);

        // Then
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    @DisplayName("Should correctly map exercise DTOs to entities")
    void shouldCorrectlyMapExerciseDtosToEntities() {
        // Given
        WorkoutDto dto = new WorkoutDto();
        dto.setUserId(1L);
        dto.setDate(LocalDate.now());

        ExerciseDto ex1 = new ExerciseDto();
        ex1.setExercise("Squat");
        ex1.setSets("4");
        ex1.setReps("8");
        ex1.setWeight("100.0");
        ex1.setWeightUnit("kg");

        ExerciseDto ex2 = new ExerciseDto();
        ex2.setExercise("Running");
        ex2.setTime("30");

        dto.setExercises(Arrays.asList(ex1, ex2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.save(any(Workout.class))).thenReturn(testWorkout);
        when(exerciseRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Exercise> exercises = invocation.getArgument(0);
            assertThat(exercises).hasSize(2);
            assertThat(exercises.get(0).getExercise()).isEqualTo("Squat");
            assertThat(exercises.get(0).getSets()).isEqualTo("4");
            assertThat(exercises.get(1).getExercise()).isEqualTo("Running");
            assertThat(exercises.get(1).getTime()).isEqualTo("30");
            return exercises;
        });

        // When
        workoutService.createWorkout(dto);

        // Then
        verify(exerciseRepository).saveAll(anyList());
    }

    // ========== DELETE WORKOUT TESTS ==========

    @Test
    @DisplayName("Should delete workout successfully")
    void shouldDeleteWorkoutSuccessfully() {
        // Given
        when(workoutRepository.existsById(1L)).thenReturn(true);
        doNothing().when(workoutRepository).deleteById(1L);

        // When
        boolean result = workoutService.deleteWorkout(1L);

        // Then
        assertThat(result).isTrue();
        verify(workoutRepository).existsById(1L);
        verify(workoutRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent workout")
    void shouldReturnFalseWhenDeletingNonExistent() {
        // Given
        when(workoutRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = workoutService.deleteWorkout(999L);

        // Then
        assertThat(result).isFalse();
        verify(workoutRepository, never()).deleteById(anyLong());
    }

    // ========== TO DTO TESTS ==========

    @Test
    @DisplayName("Should convert Workout to WorkoutDto correctly")
    void shouldConvertWorkoutToDto() {
        // Given
        testWorkout.setExercises(Arrays.asList(testExercise));
        when(workoutRepository.findByUserIdOrderByDateDesc(1L))
                .thenReturn(Arrays.asList(testWorkout));

        // When
        List<WorkoutDto> results = workoutService.getWorkoutsByUser(1L);

        // Then
        assertThat(results).hasSize(1);
        WorkoutDto dto = results.get(0);
        assertThat(dto.getId()).isEqualTo(testWorkout.getId());
        assertThat(dto.getUserId()).isEqualTo(testUser.getId());
        assertThat(dto.getDate()).isEqualTo(testWorkout.getDate());
        assertThat(dto.getNotes()).isEqualTo(testWorkout.getNotes());
        assertThat(dto.getExercises()).hasSize(1);
    }

    @Test
    @DisplayName("Should convert exercises to exercise DTOs correctly")
    void shouldConvertExercisesToDtos() {
        // Given
        testWorkout.setExercises(Arrays.asList(testExercise));
        when(workoutRepository.findByUserIdOrderByDateDesc(1L))
                .thenReturn(Arrays.asList(testWorkout));

        // When
        List<WorkoutDto> results = workoutService.getWorkoutsByUser(1L);

        // Then
        WorkoutDto dto = results.get(0);
        ExerciseDto exerciseDto = dto.getExercises().get(0);
        assertThat(exerciseDto.getId()).isEqualTo(testExercise.getId());
        assertThat(exerciseDto.getExercise()).isEqualTo(testExercise.getExercise());
        assertThat(exerciseDto.getSets()).isEqualTo(testExercise.getSets());
        assertThat(exerciseDto.getReps()).isEqualTo(testExercise.getReps());
        assertThat(exerciseDto.getWeight()).isEqualTo(testExercise.getWeight());
        assertThat(exerciseDto.getWeightUnit()).isEqualTo(testExercise.getWeightUnit());
    }

    @Test
    @DisplayName("Should handle workout with null exercises")
    void shouldHandleWorkoutWithNullExercises() {
        // Given
        testWorkout.setExercises(null);
        when(workoutRepository.findByUserIdOrderByDateDesc(1L))
                .thenReturn(Arrays.asList(testWorkout));

        // When
        List<WorkoutDto> results = workoutService.getWorkoutsByUser(1L);

        // Then
        WorkoutDto dto = results.get(0);
        assertThat(dto.getExercises()).isNull();
    }

    @Test
    @DisplayName("Should handle workout with null user")
    void shouldHandleWorkoutWithNullUser() {
        // Given
        testWorkout.setUser(null);
        when(workoutRepository.findByUserIdOrderByDateDesc(1L))
                .thenReturn(Arrays.asList(testWorkout));

        // When
        List<WorkoutDto> results = workoutService.getWorkoutsByUser(1L);

        // Then
        WorkoutDto dto = results.get(0);
        assertThat(dto.getUserId()).isNull();
    }
}
