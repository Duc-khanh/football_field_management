package com.example.football_field_management.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
}
