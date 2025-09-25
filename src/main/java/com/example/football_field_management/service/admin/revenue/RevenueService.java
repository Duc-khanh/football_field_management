package com.example.football_field_management.service.admin.revenue;

import com.example.football_field_management.dto.CustomerSpentDTO;
import com.example.football_field_management.model.OrderPayment;
import com.example.football_field_management.repository.OrderPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // ========== Biểu đồ doanh thu ==========
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

    // ========== Tổng doanh thu / đơn / khách ==========
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

    // ======= Doanh thu/Đơn/Khách theo NĂM =======
    @Override
    public BigDecimal getRevenueByYear(int year) {
        return orderPaymentRepository.sumRevenueByYear(year);
    }

    @Override
    public List<OrderPayment> getOrdersByYear(int year) {
        return orderPaymentRepository.findByYear(year);
    }

    @Override
    public long getUniqueBuyersByYear(int year) {
        return orderPaymentRepository.countDistinctBuyersByYear(year);
    }

    // ========== Đơn hàng ==========
    @Override
    public List<OrderPayment> getOrdersByMonth(int year, int month) {
        return orderPaymentRepository.findByYearAndMonth(year, month);
    }

    @Override
    public List<OrderPayment> getAllOrderPayments() {
        return orderPaymentRepository.findAll();
    }

    // ========== Doanh thu ngày ==========
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
    public long getUniqueBuyers(int year, int month) {
        return orderPaymentRepository.countDistinctBuyersByYearAndMonth(year, month);
    }
    @Override
    public BigDecimal getYesterdayRevenue() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return orderPaymentRepository.getRevenueByDay(yesterday);
    }
    @Override
    public Page<OrderPayment> getOrders(int page, int size, Integer year, Integer month) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return orderPaymentRepository.findOrdersByYearAndMonth(year, month, pageable);
    }
    @Override
    public List<CustomerSpentDTO> getCustomerSpent(int limit, Integer year, Integer month) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderPaymentRepository.findCustomerSpentByYearMonth(year, month, pageable);
    }
    @Override
    public long getTodayOrders() {
        return orderPaymentRepository.countTodayOrders();
    }
    @Override
    public BigDecimal getRevenueThisMonth() {
        return orderPaymentRepository.getRevenueThisMonth();
    }

}
