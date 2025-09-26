package com.example.football_field_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerSpentDTO {
    private Long userId;
    private String fullName;
    private BigDecimal totalSpent;
    private String avatar;
    private String email;
    private String address;
}
