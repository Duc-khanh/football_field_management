package com.example.football_field_management.dto;

import lombok.Data;

@Data
public class AccountResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private String provider;
    private String role;
    private Boolean status;
}