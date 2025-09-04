package com.example.football_field_management.dto;

import java.math.BigDecimal;

public interface RevenuePoint {
    String getLabel();
    BigDecimal getAmount();
    Long getOrders();
}