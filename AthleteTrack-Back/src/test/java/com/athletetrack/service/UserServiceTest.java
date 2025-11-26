package com.athletetrack.service;

import com.athletetrack.dto.UpdateProfileRequest;
import com.athletetrack.dto.UserDto;
import com.athletetrack.entity.User;
import com.athletetrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.UserRole.USER);
        testUser.setAvatar("https://example.com/avatar.jpg");
    }

    @Test
    @DisplayName("Should update user name successfully")
    void shouldUpdateNameSuccessfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Updated Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.updateProfile(1L, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("Should update user email successfully")
    void shouldUpdateEmailSuccessfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("newemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.updateProfile(1L, request);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).existsByEmail("newemail@example.com");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should update user avatar successfully")
    void shouldUpdateAvatarSuccessfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatar("https://example.com/new-avatar.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.updateProfile(1L, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAvatar()).isEqualTo("https://example.com/new-avatar.jpg");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should update all fields successfully")
    void shouldUpdateAllFieldsSuccessfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("New Name");
        request.setEmail("newemail@example.com");
        request.setAvatar("https://example.com/new-avatar.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.updateProfile(1L, request);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).existsByEmail("newemail@example.com");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("New Name");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> userService.updateProfile(999L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email is already taken")
    void shouldThrowExceptionWhenEmailAlreadyTaken() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("taken@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> userService.updateProfile(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El email ya está en uso");

        verify(userRepository).existsByEmail("taken@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should not check email when email is unchanged")
    void shouldNotCheckEmailWhenUnchanged() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("test@example.com"); // Same as current email

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.updateProfile(1L, request);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should correctly convert User to UserDto")
    void shouldConvertUserToDto() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("New Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.updateProfile(1L, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getRole()).isEqualTo(testUser.getRole());
        assertThat(result.getAvatar()).isEqualTo(testUser.getAvatar());
    }

    @Test
    @DisplayName("Should trim whitespace from name")
    void shouldTrimWhitespaceFromName() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("  Trimmed Name  ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateProfile(1L, request);

        // Then
        assertThat(testUser.getName()).isEqualTo("Trimmed Name");
    }

    @Test
    @DisplayName("Should trim whitespace from email and convert to lowercase")
    void shouldTrimAndLowercaseEmail() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("  NewEmail@EXAMPLE.COM  ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateProfile(1L, request);

        // Then
        assertThat(testUser.getEmail()).isEqualTo("newemail@example.com");
        verify(userRepository).existsByEmail("newemail@example.com");
    }

    @Test
    @DisplayName("Should trim whitespace from avatar")
    void shouldTrimWhitespaceFromAvatar() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAvatar("  https://example.com/avatar.jpg  ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateProfile(1L, request);

        // Then
        assertThat(testUser.getAvatar()).isEqualTo("https://example.com/avatar.jpg");
    }
}
