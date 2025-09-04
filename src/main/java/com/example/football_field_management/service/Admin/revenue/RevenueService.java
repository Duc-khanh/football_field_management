package com.example.football_field_management.service.Admin.revenue;

import com.example.football_field_management.dto.RevenuePoint;
import com.example.football_field_management.dto.RevenueSummary;
import com.example.football_field_management.repository.OrderPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// com.example.football_field_management.service.report.RevenueService
@Service
@RequiredArgsConstructor
public class RevenueService {
    private final OrderPaymentRepository repo;

    public enum GroupBy { DAY, MONTH, YEAR }

    public List<RevenuePoint> series(LocalDate from, LocalDate to, GroupBy groupBy) {
        LocalDateTime f = from.atStartOfDay();
        LocalDateTime t = to.plusDays(1).atStartOfDay().minusSeconds(1);
        return switch (groupBy) {
            case DAY   -> repo.revenueByDay(f, t);
            case MONTH -> repo.revenueByMonth(f, t);
            case YEAR  -> repo.revenueByYear(f, t);
        };
    }

    public RevenueSummary summary(LocalDate from, LocalDate to) {
        LocalDateTime f = from.atStartOfDay();
        LocalDateTime t = to.plusDays(1).atStartOfDay().minusSeconds(1);
        BigDecimal total = repo.totalRevenue(f, t);
        long orders = repo.totalOrders(f, t);
        BigDecimal aov = orders == 0 ? BigDecimal.ZERO :
                total.divide(BigDecimal.valueOf(orders), 2, RoundingMode.HALF_UP);
        return new RevenueSummary(total, orders, aov);
    }
}

