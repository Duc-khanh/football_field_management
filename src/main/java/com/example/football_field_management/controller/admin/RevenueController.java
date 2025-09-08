//package com.example.football_field_management.controller.admin;
//
//import com.example.football_field_management.service.admin.revenue.RevenueService;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDate;
//import java.util.Objects;
//
//
//@Controller
//@RequestMapping("/admin/revenue")
//@RequiredArgsConstructor
//public class RevenueController {
//
//    private final RevenueService revenueService;
//
//    @GetMapping
//    public String view(
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
//            @RequestParam(defaultValue = "DAY") RevenueService.GroupBy groupBy,
//            Model model) {
//
//        LocalDate now = LocalDate.now();
//        if (from == null) from = now.minusMonths(1);    // mặc định 1 tháng gần nhất
//        if (to == null)   to   = now;
//
//        var series = revenueService.series(from, to, groupBy)
//                .stream()
//                .filter(Objects::nonNull)
//                .toList();
//        var summary = revenueService.summary(from, to);
//
//        model.addAttribute("from", from);
//        model.addAttribute("to", to);
//        model.addAttribute("groupBy", groupBy);
//        model.addAttribute("series", series);
//        model.addAttribute("summary", summary);
//        return "admin/revenue/revenue-dashboard";
//    }
//
//    @GetMapping("/export")
//    public void exportCsv(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
//            @RequestParam(defaultValue = "DAY") RevenueService.GroupBy groupBy,
//            HttpServletResponse resp) throws IOException {
//
//        var series = revenueService.series(from, to, groupBy);
//
//        resp.setContentType("text/csv; charset=UTF-8");
//        resp.setHeader("Content-Disposition", "attachment; filename=revenue.csv");
//        try (var w = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8))) {
//            w.println("Label,Orders,Amount");
//            for (var p : series) {
//                w.printf("%s,%d,%s%n", p.getLabel(), p.getOrders(), p.getAmount());
//            }
//        }
//    }
//}
//
