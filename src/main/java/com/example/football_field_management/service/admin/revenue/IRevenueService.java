package com.example.football_field_management.service.admin.revenue;

import com.example.football_field_management.dto.MonthlyRevenueDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IRevenueService {
    Map<String, BigDecimal[]> getMonthlyRevenue(int year);
}
