package com.athletetrack.controller;

import com.athletetrack.dto.UpdateProfileRequest;
import com.athletetrack.dto.UserDto;
import com.athletetrack.entity.User;
import com.athletetrack.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;
    private UpdateProfileRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setRole(User.UserRole.USER);

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setEmail("updated@example.com");
        testUserDto.setName("Updated Name");
        testUserDto.setUsername("testuser");
        testUserDto.setRole(User.UserRole.USER);

        updateRequest = new UpdateProfileRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setEmail("updated@example.com");
    }

    @Test
    @DisplayName("Should update profile successfully with authenticated user")
    @WithMockUser
    void shouldUpdateProfileSuccessfully() throws Exception {
        // Given
        Authentication auth = createAuthenticationWithUser(testUser);
        when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
                .thenReturn(testUserDto);

        // When / Then
        mockMvc.perform(put("/api/users/profile")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.name", is("Updated Name")));

        verify(userService).updateProfile(eq(1L), any(UpdateProfileRequest.class));
    }

    @Test
    @DisplayName("Should return 401 when no authentication")
    void shouldReturn401WhenNoAuthentication() throws Exception {
        // When / Then
        mockMvc.perform(put("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    @DisplayName("Should return 401 when authentication principal is null")
    @WithMockUser
    void shouldReturn401WhenPrincipalIsNull() throws Exception {
        // Given
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(null);

        // When / Then
        mockMvc.perform(put("/api/users/profile")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    @DisplayName("Should return 401 when principal is not a User instance")
    @WithMockUser
    void shouldReturn401WhenPrincipalNotUser() throws Exception {
        // Given
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("not-a-user-object");

        // When / Then
        mockMvc.perform(put("/api/users/profile")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    @DisplayName("Should return 400 when service throws RuntimeException")
    @WithMockUser
    void shouldReturn400WhenServiceThrowsException() throws Exception {
        // Given
        Authentication auth = createAuthenticationWithUser(testUser);
        when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
                .thenThrow(new RuntimeException("El email ya está en uso"));

        // When / Then
        mockMvc.perform(put("/api/users/profile")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("El email ya está en uso")));

        verify(userService).updateProfile(eq(1L), any(UpdateProfileRequest.class));
    }

    @Test
    @DisplayName("Should validate request body with valid UpdateProfileRequest")
    @WithMockUser
    void shouldValidateRequestBody() throws Exception {
        // Given
        UpdateProfileRequest validRequest = new UpdateProfileRequest();
        validRequest.setName("Valid Name");
        validRequest.setEmail("valid@example.com");

        Authentication auth = createAuthenticationWithUser(testUser);
        when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
                .thenReturn(testUserDto);

        // When / Then
        mockMvc.perform(put("/api/users/profile")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).updateProfile(eq(1L), any(UpdateProfileRequest.class));
    }

    /**
     * Helper method to create a mock Authentication with a User principal
     */
    private Authentication createAuthenticationWithUser(User user) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        return auth;
    }
}
