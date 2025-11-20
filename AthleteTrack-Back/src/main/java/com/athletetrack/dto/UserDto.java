package com.athletetrack.dto;

import com.athletetrack.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String username;
    private User.UserRole role;
    private String avatar;
}
