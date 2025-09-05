package com.example.football_field_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RevenueSummary {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private BigDecimal avgOrderValue; // = totalRevenue / totalOrders
}