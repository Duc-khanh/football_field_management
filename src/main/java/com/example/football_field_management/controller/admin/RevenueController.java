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
    private final ObjectMapper objectMapper; // Jackson ObjectMapper để convert Map -> JSON

    @GetMapping
    public String revenuePage(Model model) throws JsonProcessingException {
        int currentYear = Year.now().getValue();
        List<Integer> years = IntStream.rangeClosed(currentYear - 5, currentYear)
                .boxed()
                .toList();
        Map<String, BigDecimal[]> revenueMap = revenueService.getMonthlyRevenue(currentYear);
        String revenueMapJson = objectMapper.writeValueAsString(revenueMap);

        List<OrderPayment> orders = revenueService.getAllOrderPayments();

        long pendingCount = orders.stream()
                .filter(o -> o.getStatus() == OrderPayment.Status.PAID)
                .count();

        long confirmedCount = orders.stream()
                .filter(o -> o.getStatus() == OrderPayment.Status.COMPLETE)
                .count();

        long completedCount = orders.stream()
                .filter(o -> o.getStatus() == OrderPayment.Status.COMPLETE)
                .count();

        long cancelledCount = orders.stream()
                .filter(o -> o.getStatus() == OrderPayment.Status.CANCELLED)
                .count();

        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("cancelledCount", cancelledCount);
        model.addAttribute("cancelledCount", cancelledCount);
        model.addAttribute("years", years);
        model.addAttribute("year", currentYear);
        model.addAttribute("revenueMapJson", revenueMapJson);
        model.addAttribute("cancelledCount", cancelledCount);

        return "admin/revenue/revenue-dashboard";
    }


    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getRevenueData(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {

        Map<String, Object> result = new HashMap<>();

        if (month == null) {
            // Trả về doanh thu 12 tháng
            List<String> labels = IntStream.rangeClosed(1, 12)
                    .mapToObj(m -> "Tháng " + m)
                    .toList();
            List<BigDecimal> values = revenueService.getRevenueByMonth(year); // size = 12
            result.put("labels", labels);
            result.put("values", values);
        } else {
            int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
            List<String> labels = IntStream.rangeClosed(1, daysInMonth)
                    .mapToObj(d -> " " + d)
                    .toList();
            List<BigDecimal> values = revenueService.getRevenueByDay(year, month, daysInMonth);
            result.put("labels", labels);
            result.put("values", values);
        }

        return result;
    }
}
