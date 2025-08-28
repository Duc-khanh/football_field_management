package com.example.football_field_management.controller.authenticate;


import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.dto.AuthResponse;
import com.example.football_field_management.service.User.AccountService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginControllerT {

    private final AccountService accountService;

    // Hiển thị trang login
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login"; // trả về login.html trong /templates
    }

    // Xử lý login
    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") LoginRequest loginRequest,
                        Model model, HttpSession session) {
        try {
            AuthResponse authResponse = accountService.login(loginRequest);

            // lưu thông tin user vào session
            session.setAttribute("user", authResponse);

            // chuyển hướng sang trang chủ (home.html)
            return "redirect:/admin/homeAdmin";
        } catch (Exception e) {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu!");
            return "login";
        }
    }

    // Xử lý logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:auth/login";
    }
}
