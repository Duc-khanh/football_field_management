package com.example.football_field_management.service.admin.revenue;

import com.example.football_field_management.dto.MonthlyRevenueDTO;
import com.example.football_field_management.model.OrderPayment;
import com.example.football_field_management.repository.OrderPaymentRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
}

