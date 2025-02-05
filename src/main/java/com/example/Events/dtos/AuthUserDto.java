package com.example.Events.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthUserDto {
    @NotBlank(message = "The username is required")
    @Size(min = 3, max = 50, message = "The length of the username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "The password is required")
    @Size(min = 5, message = "The password must be at least 5 characters long")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
