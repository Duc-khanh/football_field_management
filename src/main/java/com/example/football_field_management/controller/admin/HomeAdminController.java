package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class HomeAdminController {
    private final AccountRepository accountRepo;
    @GetMapping
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Account account;
        if (username.contains("@")) {
            account = accountRepo.findByEmail(username).orElse(null);
        } else {
            account = accountRepo.findByPhone(username).orElse(null);
        }

        if (account != null) {
            model.addAttribute("fullName", account.getFullName());
            model.addAttribute("emailOrPhone", account.getEmail() != null ? account.getEmail() : account.getPhone());
            model.addAttribute("avatar", account.getAvt_path() != null ? account.getAvt_path() : "/images/avatar.png");
        }

        return "admin/home";
    }

}
