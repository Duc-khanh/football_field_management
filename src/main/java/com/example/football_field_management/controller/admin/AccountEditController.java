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

@Controller
@RequestMapping("/admin/account")
@RequiredArgsConstructor
public class AccountEditController {

    private final AccountRepository accountRepo;

    @GetMapping
    public String editProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Account account;
        if (username.contains("@")) {
            account = accountRepo.findByEmail(username).orElse(null);
        } else {
            account = accountRepo.findByPhone(username).orElse(null);
        }

        if (account == null) {
            return "redirect:/login"; // hoặc xử lý lỗi phù hợp
        }

        model.addAttribute("account", account);
        return "admin/account/profile"; // tên file HTML của bạn
    }
}
