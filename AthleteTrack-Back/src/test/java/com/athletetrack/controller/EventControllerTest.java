package com.athletetrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createEventValidation() throws Exception {
        // register and login
        String suffix = String.valueOf(System.currentTimeMillis() % 1000000);
        String email = "event@example.com";
        String username = "evt" + suffix;

        var register = new java.util.HashMap<String, Object>();
        register.put("email", email);
        register.put("password", "eventpass123");
        register.put("name", "Event E2E");
        register.put("username", username);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        var login = new java.util.HashMap<String, Object>();
        login.put("email", email);
        login.put("password", "eventpass123");

        var loginResp = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResp.getResponse().getContentAsString()).get("token").asText();

        // Missing title -> should return 400 or 500 depending on validation
        var badEvent = new java.util.HashMap<String, Object>();
        badEvent.put("description", "no title");
        badEvent.put("date", "2025-12-31");
        badEvent.put("location", "Nowhere");

        mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badEvent)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
