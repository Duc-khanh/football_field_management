package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.OrderPayment;
import com.example.football_field_management.service.admin.revenue.IRevenueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final IRevenueService revenueService;
    private final ObjectMapper objectMapper;

    /**
     * Trang dashboard doanh thu
     */
    @GetMapping
    public String revenuePage(Model model) throws JsonProcessingException {
        int currentYear = Year.now().getValue();
        int currentMonth = LocalDate.now().getMonthValue();

        // Danh sách năm để filter chart
        List<Integer> years = IntStream.rangeClosed(currentYear - 5, currentYear)
                .boxed()
                .toList();

        // Dữ liệu chart doanh thu theo tháng
        Map<String, BigDecimal[]> revenueMap = revenueService.getMonthlyRevenue(currentYear);
        String revenueMapJson = objectMapper.writeValueAsString(revenueMap);

        // ---- Doanh thu ----
        BigDecimal todayRevenue = revenueService.getTodayRevenue();
        // lấy doanh thu của ngày hôm qua bằng hàm có sẵn: getRevenueByDate
        BigDecimal yesterdayRevenue = revenueService.getRevenueByDate(LocalDate.now().minusDays(1));

        // bảo đảm không null
        if (todayRevenue == null) todayRevenue = BigDecimal.ZERO;
        if (yesterdayRevenue == null) yesterdayRevenue = BigDecimal.ZERO;

        // tính phần trăm tăng/giảm (2 chữ số thập phân)
        BigDecimal growthPercent;
        if (yesterdayRevenue.compareTo(BigDecimal.ZERO) == 0) {
            // nếu hôm qua = 0
            growthPercent = todayRevenue.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(100); // tuỳ bạn có muốn +100% khi hôm qua = 0
        } else {
            growthPercent = todayRevenue.subtract(yesterdayRevenue)
                    .divide(yesterdayRevenue, 4, RoundingMode.HALF_UP) // chia chính xác
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP); // lấy 2 chữ số sau dấu phẩy
        }

        // build label và class (tính sẵn, tránh logic trong Thymeleaf)
        boolean isUp = growthPercent.compareTo(BigDecimal.ZERO) > 0;
        boolean isDown = growthPercent.compareTo(BigDecimal.ZERO) < 0;
        String growthClass;
        String growthLabel;
        if (isUp) {
            growthClass = "text-success fw-bold";
            growthLabel = "+" + growthPercent.toPlainString() + "%";
        } else if (isDown) {
            growthClass = "text-danger fw-bold";
            // growthPercent.toPlainString() đã có dấu "-" nếu âm
            growthLabel = growthPercent.toPlainString() + "%";
        } else {
            growthClass = "text-secondary";
            growthLabel = "0%";
        }

        BigDecimal monthRevenue = revenueService.getMonthRevenue(currentYear, currentMonth);
        BigDecimal totalRevenue = revenueService.getTotalRevenue();

        // ---- Danh sách order tháng hiện tại ----
        List<OrderPayment> ordersThisMonth = revenueService.getOrdersByMonth(currentYear, currentMonth);

        // ---- Danh sách người mua hàng ----
        long buyersThisMonth = revenueService.getUniqueBuyers(currentYear, currentMonth);

        // Đẩy dữ liệu sang view
        model.addAttribute("buyersThisMonth", buyersThisMonth);
        model.addAttribute("isUp", isUp);
        model.addAttribute("isDown", isDown);
        model.addAttribute("growthLabel", growthLabel);     // chuỗi đã format để hiển thị
        model.addAttribute("growthClass", growthClass);     // chuỗi class đã chọn
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("monthRevenue", monthRevenue);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("ordersThisMonth", ordersThisMonth);
        model.addAttribute("years", years);
        model.addAttribute("year", currentYear);
        model.addAttribute("revenueMapJson", revenueMapJson);

        return "admin/revenue/revenue-dashboard";
    }


    /**
     * API trả dữ liệu chart + số lượng đơn theo status
     */
    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getRevenueData(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {

        Map<String, Object> result = new HashMap<>();

        // 1️⃣ Labels + doanh thu
        List<String> labels;
        List<BigDecimal> values;

        if (month == null) {
            labels = IntStream.rangeClosed(1, 12)
                    .mapToObj(m -> "Tháng " + m)
                    .toList();
            values = revenueService.getRevenueByMonth(year);
        } else {
            int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
            labels = IntStream.rangeClosed(1, daysInMonth)
                    .mapToObj(d -> "Ngày " + d)
                    .toList();
            values = revenueService.getRevenueByDay(year, month, daysInMonth);
        }

        result.put("labels", labels);
        result.put("values", values);

        // 2️⃣ Số lượng đơn theo trạng thái
        List<OrderPayment> orders = revenueService.getAllOrderPayments();
        if (month != null) {
            orders = orders.stream()
                    .filter(o -> o.getPaidAt() != null
                            && o.getPaidAt().getYear() == year
                            && o.getPaidAt().getMonthValue() == month)
                    .toList();
        } else {
            orders = orders.stream()
                    .filter(o -> o.getPaidAt() != null
                            && o.getPaidAt().getYear() == year)
                    .toList();
        }

        long paidCount = orders.stream()
                .filter(o -> o.getStatus() == OrderPayment.Status.PAID)
                .count();
        long completeCount = orders.stream()
                .filter(o -> o.getStatus() == OrderPayment.Status.COMPLETE)
                .count();
        long cancelledCount = orders.stream()
                .filter(o -> o.getStatus() == OrderPayment.Status.CANCELLED)
                .count();
        long refundedCount = orders.stream()
                .filter(o -> o.getStatus() == OrderPayment.Status.REFUNDED)
                .count();

        result.put("paidCount", paidCount);
        result.put("completeCount", completeCount);
        result.put("cancelledCount", cancelledCount);
        result.put("refundedCount", refundedCount);

        return result;
    }

    /**
     * API trả dữ liệu tổng quan (revenue / orders / buyers)
     * -> dùng để update các thẻ số liệu khi lọc
     */
    @GetMapping("/summary")
    @ResponseBody
    public Map<String, Object> getSummary(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {

        Map<String, Object> result = new HashMap<>();

        int y = year;
        int m = (month != null ? month : LocalDate.now().getMonthValue());

        BigDecimal todayRevenue = revenueService.getTodayRevenue();
        BigDecimal yesterdayRevenue = revenueService.getRevenueByDate(LocalDate.now().minusDays(1));
        if (todayRevenue == null) todayRevenue = BigDecimal.ZERO;
        if (yesterdayRevenue == null) yesterdayRevenue = BigDecimal.ZERO;

        BigDecimal growthPercent;
        if (yesterdayRevenue.compareTo(BigDecimal.ZERO) == 0) {
            growthPercent = todayRevenue.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(100);
        } else {
            growthPercent = todayRevenue.subtract(yesterdayRevenue)
                    .divide(yesterdayRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        boolean isUp = growthPercent.compareTo(BigDecimal.ZERO) > 0;
        boolean isDown = growthPercent.compareTo(BigDecimal.ZERO) < 0;

        BigDecimal totalRevenue;
        if(month != null) {
            totalRevenue = revenueService.getMonthRevenue(year, month);
        } else {
            totalRevenue = revenueService.getRevenueByYear(year);
        }
        List<OrderPayment> ordersThisMonth = revenueService.getOrdersByMonth(y, m);
        long buyersThisMonth = revenueService.getUniqueBuyers(y, m);

        result.put("todayRevenue", todayRevenue);
        result.put("growthPercent", growthPercent);
        result.put("isUp", isUp);
        result.put("isDown", isDown);
        result.put("totalRevenue", totalRevenue);
        result.put("orders", ordersThisMonth.size());
        result.put("buyers", buyersThisMonth);

        return result;
    }

}
