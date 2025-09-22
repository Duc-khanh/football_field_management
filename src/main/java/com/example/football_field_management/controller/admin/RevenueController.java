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
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
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
        BigDecimal growthPercent = revenueService.getTodayRevenueGrowthPercent();
        boolean isUp = growthPercent.compareTo(BigDecimal.ZERO) > 0;
        boolean isDown = growthPercent.compareTo(BigDecimal.ZERO) < 0;        BigDecimal monthRevenue = revenueService.getMonthRevenue(currentYear, currentMonth);
        BigDecimal totalRevenue = revenueService.getTotalRevenue();

        // ---- Danh sách order tháng hiện tại ----
        List<OrderPayment> ordersThisMonth = revenueService.getOrdersByMonth(currentYear, currentMonth);

        // ---- Danh sách người mua hàng ----
        long buyersThisMonth = revenueService.getUniqueBuyers(currentYear, currentMonth);
        // Đẩy dữ liệu sang view
        model.addAttribute("buyersThisMonth",buyersThisMonth);
        model.addAttribute("isUp", isUp);
        model.addAttribute("isDown", isDown);
        model.addAttribute("growthPercent", growthPercent);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("monthRevenue", monthRevenue);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("ordersThisMonth", ordersThisMonth);
        model.addAttribute("years", years);
        model.addAttribute("year", currentYear);
        model.addAttribute("revenueMapJson", revenueMapJson);

        return "admin/revenue/revenue-dashboard";
    }


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
            labels = IntStream.rangeClosed(1, 12).mapToObj(m -> "Tháng " + m).toList();
            values = revenueService.getRevenueByMonth(year);
        } else {
            int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
            labels = IntStream.rangeClosed(1, daysInMonth).mapToObj(d -> "Ngày " + d).toList();
            values = revenueService.getRevenueByDay(year, month, daysInMonth);
        }

        result.put("labels", labels);
        result.put("values", values);

        // 2️⃣ Tính số lượng đơn theo trạng thái trong khoảng filter
        List<OrderPayment> orders = revenueService.getAllOrderPayments();
        if (month != null) {
            orders = orders.stream()
                    .filter(o -> o.getPaidAt() != null &&
                            o.getPaidAt().getYear() == year &&
                            o.getPaidAt().getMonthValue() == month)
                    .toList();
        } else {
            orders = orders.stream()
                    .filter(o -> o.getPaidAt() != null && o.getPaidAt().getYear() == year)
                    .toList();
        }

        long paidCount = orders.stream().filter(o -> o.getStatus() == OrderPayment.Status.PAID).count();
        long completeCount = orders.stream().filter(o -> o.getStatus() == OrderPayment.Status.COMPLETE).count();
        long cancelledCount = orders.stream().filter(o -> o.getStatus() == OrderPayment.Status.CANCELLED).count();
        long refundedCount = orders.stream().filter(o -> o.getStatus() == OrderPayment.Status.REFUNDED).count();

        result.put("paidCount", paidCount);
        result.put("completeCount", completeCount);
        result.put("cancelledCount", cancelledCount);
        result.put("refundedCount", refundedCount);

        return result;
    }
}
