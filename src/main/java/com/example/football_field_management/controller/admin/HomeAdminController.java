package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.repository.RoleRepository;
import com.example.football_field_management.repository.VenueRepository;
import com.example.football_field_management.service.admin.revenue.IRevenueService;
import com.example.football_field_management.service.admin.revenue.RevenueService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class HomeAdminController {

    private final AccountRepository accountRepo;
    private final VenueRepository venueRepo;
    private final IRevenueService revenueService;

    @GetMapping
    public String home(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String message = (String) session.getAttribute("successMessage");
        if (message != null) {
            model.addAttribute("successMessage", message);
            session.removeAttribute("successMessage");
        }

        Account account = username.contains("@")
                ? accountRepo.findByEmail(username).orElse(null)
                : accountRepo.findByPhone(username).orElse(null);

        if (account != null) {
            model.addAttribute("fullName", account.getFullName());
            model.addAttribute("emailOrPhone",
                    account.getEmail() != null ? account.getEmail() : account.getPhone());
            model.addAttribute("avatar",
                    account.getAvt_path() != null ? account.getAvt_path() : "/images/avatar.png");

            // 👉 Thêm role vào Model
            // Giả sử Account có getRole() trả về "ADMIN" hoặc "OWNER"
            if (account.getRoles() != null) {
                model.addAttribute("role", account.getRoles());
            } else {
                model.addAttribute("role", "GUEST"); // mặc định
            }
        }

        // 👉 Dữ liệu thống kê
        model.addAttribute("todayOrders", revenueService.getTodayOrders());
        model.addAttribute("revenueThisMonth", revenueService.getRevenueThisMonth());
        model.addAttribute("totalUsers", accountRepo.count());
        model.addAttribute("totalVenues", venueRepo.count());

        return "admin/home";
    }

}
