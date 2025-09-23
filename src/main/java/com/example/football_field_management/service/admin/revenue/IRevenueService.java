package com.example.football_field_management.service.admin.revenue;

import com.example.football_field_management.dto.MonthlyRevenueDTO;
import com.example.football_field_management.model.OrderPayment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IRevenueService {
    Map<String, BigDecimal[]> getMonthlyRevenue(int year);

    /** Lấy doanh thu theo từng tháng trong năm */
    List<BigDecimal> getRevenueByMonth(int year);

    /** Lấy doanh thu từng ngày trong tháng (đủ số ngày, ngày không có dữ liệu = 0) */
    List<BigDecimal> getRevenueByDay(int year, int month, int daysInMonth);
    List<OrderPayment> getAllOrderPayments();
    BigDecimal getTodayRevenue();
    BigDecimal getMonthRevenue(int year, int month);
    BigDecimal getTotalRevenue();
    List<OrderPayment> getOrdersByMonth(int year, int month);
    BigDecimal getRevenueByDate(LocalDate date);


    long getUniqueBuyers(int year, int month);
    BigDecimal getRevenueByYear(int year);
    List<OrderPayment> getOrdersByYear(int year);
    long getUniqueBuyersByYear(int year);
    BigDecimal getYesterdayRevenue();

}
