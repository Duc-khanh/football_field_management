package com.example.football_field_management.dto;

import com.example.football_field_management.model.OrderPayment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDTO {
    private Long month;
    private OrderPayment.Status status;
    private BigDecimal total;
}
