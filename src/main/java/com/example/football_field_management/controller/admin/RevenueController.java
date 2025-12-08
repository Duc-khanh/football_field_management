package com.example.football_field_management.controller.admin;

import com.example.football_field_management.dto.CustomerSpentDTO;
import com.example.football_field_management.model.OrderPayment;
import com.example.football_field_management.repository.OrderPaymentRepository;
import com.example.football_field_management.service.admin.revenue.IRevenueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.Optional;
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
    public String revenuePage(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @RequestParam(defaultValue = "5") int limit,
                              @RequestParam(required = false) Integer year,
                              @RequestParam(required = false) Integer month,
                              Model model) throws JsonProcessingException {
        int selectedYear = (year != null) ? year : Year.now().getValue();
        int selectedMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        // --- Chart filter ---
        List<Integer> years = IntStream.rangeClosed(selectedYear - 5, selectedYear)
                .boxed()
                .toList();
        Map<String, BigDecimal[]> revenueMap = revenueService.getMonthlyRevenue(selectedYear);
        String revenueMapJson = objectMapper.writeValueAsString(revenueMap);

        // --- Revenue ---
        BigDecimal todayRevenue = Optional.ofNullable(revenueService.getTodayRevenue()).orElse(BigDecimal.ZERO);
        BigDecimal yesterdayRevenue = Optional.ofNullable(revenueService.getRevenueByDate(LocalDate.now().minusDays(1))).orElse(BigDecimal.ZERO);

        BigDecimal growthPercent;
        if (yesterdayRevenue.compareTo(BigDecimal.ZERO) == 0) {
            growthPercent = todayRevenue.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100);
        } else {
            growthPercent = todayRevenue.subtract(yesterdayRevenue)
                    .divide(yesterdayRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        String growthClass, growthLabel;
        if (growthPercent.compareTo(BigDecimal.ZERO) > 0) {
            growthClass = "text-success fw-bold";
            growthLabel = "+" + growthPercent.toPlainString() + "%";
        } else if (growthPercent.compareTo(BigDecimal.ZERO) < 0) {
            growthClass = "text-danger fw-bold";
            growthLabel = growthPercent.toPlainString() + "%";
        } else {
            growthClass = "text-secondary";
            growthLabel = "0%";
        }

        BigDecimal monthRevenue = revenueService.getMonthRevenue(selectedYear, selectedMonth);
        BigDecimal totalRevenue = revenueService.getTotalRevenue();

        // --- Orders & Customers ---
        Page<OrderPayment> orderPage = revenueService.getOrders(page, size, selectedYear, selectedMonth);
        List<CustomerSpentDTO> customers = revenueService.getCustomerSpent(limit, selectedYear, selectedMonth);

        long buyersThisMonth = revenueService.getUniqueBuyers(selectedYear, selectedMonth);
        List<OrderPayment> ordersThisMonth = revenueService.getOrdersByMonth(selectedYear, selectedMonth);

        // --- Add to model ---
        model.addAttribute("orders", orderPage);
        model.addAttribute("customers", customers);
        model.addAttribute("buyersThisMonth", buyersThisMonth);
        model.addAttribute("isUp", growthPercent.compareTo(BigDecimal.ZERO) > 0);
        model.addAttribute("isDown", growthPercent.compareTo(BigDecimal.ZERO) < 0);
        model.addAttribute("growthLabel", growthLabel);
        model.addAttribute("growthClass", growthClass);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("monthRevenue", monthRevenue);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("ordersThisMonth", ordersThisMonth);
        model.addAttribute("years", years);
        model.addAttribute("year", selectedYear);
        model.addAttribute("month", selectedMonth);
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

        int m = (month != null ? month : LocalDate.now().getMonthValue());

        // --- Hôm nay vs hôm qua ---
        BigDecimal todayRevenue = revenueService.getTodayRevenue();
        BigDecimal yesterdayRevenue = revenueService.getRevenueByDate(LocalDate.now().minusDays(1));

        BigDecimal growthToday = calcGrowth(todayRevenue, yesterdayRevenue);

        // --- Tổng doanh thu so với kỳ trước ---
        BigDecimal totalRevenue = month != null
                ? revenueService.getMonthRevenue(year, m)
                : revenueService.getRevenueByYear(year);

        BigDecimal previousRevenue = month != null
                ? revenueService.getMonthRevenue(year, m - 1 > 0 ? m - 1 : 12)
                : revenueService.getRevenueByYear(year - 1);

        BigDecimal growthTotalRevenue = calcGrowth(totalRevenue, previousRevenue);

        // --- Tổng đơn hàng ---
        int orders = revenueService.getOrdersByMonth(year, m).size();
        int previousOrders = revenueService.getOrdersByMonth(month != null && m > 1 ? year : year-1, month != null && m > 1 ? m-1 : 12).size();
        BigDecimal growthOrders = calcGrowth(BigDecimal.valueOf(orders), BigDecimal.valueOf(previousOrders));

        // --- Tổng người mua ---
        long buyers = revenueService.getUniqueBuyers(year, m);
        long previousBuyers = revenueService.getUniqueBuyers(month != null && m > 1 ? year : year-1, month != null && m > 1 ? m-1 : 12);
        BigDecimal growthBuyers = calcGrowth(BigDecimal.valueOf(buyers), BigDecimal.valueOf(previousBuyers));

        // Push data
        result.put("todayRevenue", todayRevenue);
        result.put("growthToday", growthToday);

        result.put("totalRevenue", totalRevenue);
        result.put("growthTotalRevenue", growthTotalRevenue);

        result.put("orders", orders);
        result.put("growthOrders", growthOrders);

        result.put("buyers", buyers);
        result.put("growthBuyers", growthBuyers);

        return result;
    }
    @GetMapping("/dashboard-data")
    @ResponseBody
    public Map<String, Object> getDashboardData(
            @RequestParam int year,
            @RequestParam(required = false) Integer month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "5") int limit
    ) {
        Map<String, Object> result = new HashMap<>();

        int selectedMonth = (month != null ? month : LocalDate.now().getMonthValue());

        // 1️⃣ Top khách hàng
        List<CustomerSpentDTO> customers = revenueService.getCustomerSpent(limit, year, month);
        result.put("customers", customers);

        // 2️⃣ Orders (phân trang thủ công)
        List<OrderPayment> allOrders = revenueService.getOrdersByMonth(year, selectedMonth);
        int start = page * size;
        int end = Math.min(start + size, allOrders.size());
        List<OrderPayment> pagedOrders = allOrders.subList(start, end);

        result.put("orders", pagedOrders);
        result.put("ordersTotalPages", (int) Math.ceil((double) allOrders.size() / size));
        result.put("ordersPage", page);

        // 3️⃣ Chart (labels + values)
        List<String> labels;
        List<BigDecimal> values;

        if (month == null) {
            // Biểu đồ theo tháng trong năm
            labels = IntStream.rangeClosed(1, 12)
                    .mapToObj(m -> "Tháng " + m)
                    .toList();
            values = revenueService.getRevenueByMonth(year);
        } else {
            // Biểu đồ theo ngày trong tháng
            int daysInMonth = YearMonth.of(year, selectedMonth).lengthOfMonth();
            labels = IntStream.rangeClosed(1, daysInMonth)
                    .mapToObj(d -> "Ngày " + d)
                    .toList();
            values = revenueService.getRevenueByDay(year, selectedMonth, daysInMonth);
        }

        result.put("labels", labels);
        result.put("values", values);

        // 4️⃣ Summary
        BigDecimal todayRevenue = revenueService.getTodayRevenue();
        BigDecimal monthRevenue = revenueService.getMonthRevenue(year, selectedMonth);
        BigDecimal totalRevenue = revenueService.getTotalRevenue();

        result.put("todayRevenue", todayRevenue);
        result.put("monthRevenue", monthRevenue);
        result.put("totalRevenue", totalRevenue);

        return result;
    }
    @GetMapping("/orders")
    @ResponseBody
    public Page<OrderPayment> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return revenueService.getOrders(page, size, year, month);
    }

    private BigDecimal calcGrowth(BigDecimal current, BigDecimal previous) {
        if(previous == null || previous.compareTo(BigDecimal.ZERO) == 0){
            return current.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100);
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
