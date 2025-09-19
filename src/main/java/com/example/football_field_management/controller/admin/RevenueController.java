package com.example.football_field_management.controller.admin;

import com.example.football_field_management.service.admin.revenue.IRevenueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RevenueController {

    private final IRevenueService revenueService;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper để convert Map -> JSON

    @GetMapping("/admin/revenue")
    public String revenuePage(Model model) throws JsonProcessingException {
        int year = Year.now().getValue();

        Map<String, BigDecimal[]> revenueMap = revenueService.getMonthlyRevenue(year);
        String revenueMapJson = objectMapper.writeValueAsString(revenueMap);

        model.addAttribute("year", year);
        model.addAttribute("revenueMapJson", revenueMapJson);

        return "admin/revenue/revenue-dashboard";
    }

}
