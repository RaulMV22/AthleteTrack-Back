package com.athletetrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndDeleteWorkout() throws Exception {
        String suffix = String.valueOf(System.currentTimeMillis() % 1000000);
        String email = "workout@example.com";
        String username = "wkt" + suffix;

        var register = new java.util.HashMap<String, Object>();
        register.put("email", email);
        register.put("password", "workoutpass123");
        register.put("name", "Workout E2E");
        register.put("username", username);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        var login = new java.util.HashMap<String, Object>();
        login.put("email", email);
        login.put("password", "workoutpass123");

        var loginResp = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResp.getResponse().getContentAsString()).get("token").asText();

        var w = new java.util.HashMap<String, Object>();
        w.put("userId", 1);
        w.put("date", "2025-11-16");
        w.put("notes", "test workout");
        var ex = new java.util.HashMap<String, Object>();
        ex.put("exercise", "Test Ex");
        ex.put("reps", "10");
        ex.put("sets", "3");
        w.put("exercises", java.util.List.of(ex));

        var createResp = mockMvc.perform(post("/api/workouts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(w)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        String body = createResp.getResponse().getContentAsString();
        var id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(delete("/api/workouts/" + id)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
