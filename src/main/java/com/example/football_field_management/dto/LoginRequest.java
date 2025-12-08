package com.example.football_field_management.dto;


import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
