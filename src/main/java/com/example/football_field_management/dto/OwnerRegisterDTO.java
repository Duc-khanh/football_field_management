package com.example.football_field_management.dto;

import lombok.Data;

@Data
public class OwnerRegisterDTO {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    // Không cần field role vì API này mặc định là đăng ký cho Owner
}
