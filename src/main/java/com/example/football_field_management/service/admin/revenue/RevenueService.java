package com.example.football_field_management.service.admin.revenue;

import com.example.football_field_management.dto.MonthlyRevenueDTO;
import com.example.football_field_management.model.OrderPayment;
import com.example.football_field_management.repository.OrderPaymentRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RevenueService implements IRevenueService {

    private final OrderPaymentRepository orderPaymentRepository;

    @Override
    public Map<String, BigDecimal[]> getMonthlyRevenue(int year) {
        BigDecimal[] revenue = new BigDecimal[12];
        Arrays.fill(revenue, BigDecimal.ZERO);

        List<Object[]> raw = orderPaymentRepository.getMonthlyRevenueComplete(year);
        for (Object[] row : raw) {
            int month = ((Number) row[0]).intValue();
            BigDecimal total = (BigDecimal) row[1];
            revenue[month - 1] = total;
        }
        Map<String, BigDecimal[]> map = new HashMap<>();
        map.put("COMPLETE", revenue);
        return map;
    }

    @Override
    public List<BigDecimal> getRevenueByMonth(int year) {
        // Tạo list mặc định 12 phần tử = 0
        List<BigDecimal> list = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));
        List<Object[]> raw = orderPaymentRepository.getMonthlyRevenueComplete(year);
        for (Object[] row : raw) {
            int month = ((Number) row[0]).intValue();
            BigDecimal total = (BigDecimal) row[1];
            list.set(month - 1, total);
        }
        return list;
    }

    @Override
    public List<BigDecimal> getRevenueByDay(int year, int month, int daysInMonth) {
        // Tạo list mặc định đủ số ngày trong tháng
        List<BigDecimal> list = new ArrayList<>(Collections.nCopies(daysInMonth, BigDecimal.ZERO));
        List<Object[]> raw = orderPaymentRepository.getDailyRevenueInMonth(year, month);
        for (Object[] row : raw) {
            int day = ((Number) row[0]).intValue();
            BigDecimal total = (BigDecimal) row[1];
            if (day >= 1 && day <= daysInMonth) {
                list.set(day - 1, total);
            }
        }
        return list;
    }

    @Override
    public List<OrderPayment> getAllOrderPayments() {
        return orderPaymentRepository.findAll();
    }
    @Override
    public BigDecimal getTodayRevenue() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return orderPaymentRepository.sumRevenueBetween(start, end);
    }

    @Override
    public BigDecimal getMonthRevenue(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = start.plusMonths(1);
        return orderPaymentRepository.sumRevenueBetween(start, end);
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return orderPaymentRepository.findAll().stream()
                .map(OrderPayment::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Override
    public List<OrderPayment> getOrdersByMonth(int year, int month) {
        return orderPaymentRepository.findByYearAndMonth(year, month);
    }
    @Override
    public BigDecimal getRevenueByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusSeconds(1);

        return orderPaymentRepository.findAll().stream()
                .filter(o -> o.getPaidAt() != null
                        && !o.getPaidAt().isBefore(start)
                        && !o.getPaidAt().isAfter(end))
                .map(OrderPayment::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTodayRevenueGrowthPercent() {
        BigDecimal today = getRevenueByDate(LocalDate.now());
        BigDecimal yesterday = getRevenueByDate(LocalDate.now().minusDays(1));

        if (yesterday.compareTo(BigDecimal.ZERO) == 0) {
            return today.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(100); // nếu hôm qua =0, hôm nay >0 thì +100%
        }

        return today.subtract(yesterday)
                .divide(yesterday, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    @Override
    public long getUniqueBuyers(int year, int month) {
        return orderPaymentRepository.countDistinctBuyersByYearAndMonth(year, month);
    }
}

