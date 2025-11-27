package com.example.football_field_management.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String address;
    private String avatar;
}