package com.example.football_field_management.controller.authenticate;


import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.security.UserDetailService;
import com.example.football_field_management.service.Admin.users.AccountService;
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
    private final UserDetailService userDetailService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:auth/login";
    }
}
