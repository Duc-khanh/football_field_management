package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final AccountRepository accountRepo;

    public GlobalControllerAdvice(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @ModelAttribute
    public void addUserInfo(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String username = auth.getName();
            Account account = username.contains("@")
                    ? accountRepo.findByEmail(username).orElse(null)
                    : accountRepo.findByPhone(username).orElse(null);

            if (account != null) {
                model.addAttribute("fullName", account.getFullName());
                model.addAttribute("emailOrPhone", account.getEmail() != null ? account.getEmail() : account.getPhone());
                model.addAttribute("avatar", account.getAvt_path() != null ? account.getAvt_path() : "/images/avatar.png");
            }
        }
    }
}

