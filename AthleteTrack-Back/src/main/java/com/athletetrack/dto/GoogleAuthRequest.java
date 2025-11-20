package com.athletetrack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoogleAuthRequest {
    @NotBlank(message = "Google token is required")
    private String token;
}
