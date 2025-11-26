package com.athletetrack.service;

import com.athletetrack.Security.JwtUtil;
import com.athletetrack.dto.AuthResponse;
import com.athletetrack.dto.LoginRequest;
import com.athletetrack.dto.RegisterRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

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
    }

    // ========== REGISTER TESTS ==========

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setName("New User");
        request.setUsername("newuser");
        request.setPassword("password123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser()).isNotNull();
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(userRepository).existsByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setUsername("newuser");
        request.setPassword("password123");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario ya existe");

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setUsername("existinguser");
        request.setPassword("password123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario ya existe");

        verify(userRepository).existsByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should encode password when registering")
    void shouldEncodePasswordWhenRegistering() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setName("Test");
        request.setUsername("testuser");
        request.setPassword("plainPassword");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        // When
        authService.register(request);

        // Then
        verify(passwordEncoder).encode("plainPassword");
    }

    @Test
    @DisplayName("Should generate JWT token on successful registration")
    void shouldGenerateJwtTokenOnRegistration() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setName("Test");
        request.setUsername("testuser");
        request.setPassword("password");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(testUser)).thenReturn("generated-jwt-token");

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertThat(response.getToken()).isEqualTo("generated-jwt-token");
        verify(jwtUtil).generateToken(testUser);
    }

    // ========== LOGIN TESTS ==========

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void shouldLoginSuccessfully() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(testUser)).thenReturn("jwt-token");

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found on login")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Credenciales inválidas");

        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void shouldThrowExceptionWhenPasswordIncorrect() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Credenciales inválidas");

        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        verify(jwtUtil, never()).generateToken(any());
    }

    // ========== GOOGLE AUTH TESTS ==========

    @Test
    @DisplayName("Should authenticate with Google token for existing user")
    void shouldAuthenticateWithGoogleTokenForExistingUser() {
        // Given
        String googleToken = createMockGoogleToken("existing@example.com", "Existing User", "https://picture.url");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(testUser)).thenReturn("jwt-token");

        // When
        AuthResponse response = authService.googleAuth(googleToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(userRepository).findByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil).generateToken(testUser);
    }

    @Test
    @DisplayName("Should create new user from Google token")
    void shouldCreateNewUserFromGoogleToken() {
        // Given
        String googleToken = createMockGoogleToken("newgoogle@example.com", "Google User", "https://picture.url");

        when(userRepository.findByEmail("newgoogle@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("newgoogle")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("randomEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        // When
        AuthResponse response = authService.googleAuth(googleToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(userRepository).findByEmail("newgoogle@example.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    @DisplayName("Should generate unique username when conflict exists")
    void shouldGenerateUniqueUsernameOnConflict() {
        // Given
        String googleToken = createMockGoogleToken("user@example.com", "User Name", null);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("user")).thenReturn(true); // First try conflicts
        when(userRepository.existsByUsername("user1")).thenReturn(false); // Second try succeeds
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        // When
        authService.googleAuth(googleToken);

        // Then
        verify(userRepository).existsByUsername("user");
        verify(userRepository).existsByUsername("user1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid Google token format")
    void shouldThrowExceptionForInvalidTokenFormat() {
        // Given
        String invalidToken = "invalid.token"; // Only 2 parts instead of 3

        // When / Then
        assertThatThrownBy(() -> authService.googleAuth(invalidToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error procesando Google token");
    }

    @Test
    @DisplayName("Should assign Google picture as avatar")
    void shouldAssignGooglePictureAsAvatar() {
        // Given
        String googleToken = createMockGoogleToken("user@example.com", "User", "https://google.com/pic.jpg");

        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail("user@example.com");
        newUser.setAvatar("https://google.com/pic.jpg");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.getAvatar()).isEqualTo("https://google.com/pic.jpg");
            return newUser;
        });
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        // When
        authService.googleAuth(googleToken);

        // Then
        verify(userRepository).save(any(User.class));
    }

    // ========== TO DTO TESTS ==========

    @Test
    @DisplayName("Should convert User to UserDto correctly")
    void shouldConvertUserToDto() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setUsername("testuser");
        user.setRole(User.UserRole.ADMIN);
        user.setAvatar("https://example.com/avatar.jpg");

        // When
        UserDto dto = authService.toDto(user);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getRole()).isEqualTo(User.UserRole.ADMIN);
        assertThat(dto.getAvatar()).isEqualTo("https://example.com/avatar.jpg");
    }

    @Test
    @DisplayName("Should return null when converting null user")
    void shouldReturnNullWhenConvertingNullUser() {
        // When
        UserDto dto = authService.toDto(null);

        // Then
        assertThat(dto).isNull();
    }

    // ========== HELPER METHODS ==========

    /**
     * Creates a mock Google ID token with the given payload
     */
    private String createMockGoogleToken(String email, String name, String picture) {
        // Create a simple JWT-like token: header.payload.signature
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"HS256\"}".getBytes());

        String payloadJson = String.format(
                "{\"email\":\"%s\",\"name\":\"%s\"%s}",
                email,
                name,
                picture != null ? ",\"picture\":\"" + picture + "\"" : "");
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString("fake-signature".getBytes());

        return header + "." + payload + "." + signature;
    }
}
