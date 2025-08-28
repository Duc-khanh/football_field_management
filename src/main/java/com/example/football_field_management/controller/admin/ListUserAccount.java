package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.service.User.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class ListUserAccount {

    private final AccountService accountService;

    @GetMapping
    public String viewAccounts(Model model) {
        model.addAttribute("accounts", accountService.getAllAccounts());
        return "account-list"; // tên file account-list.html
    }

//    @GetMapping("/{id}")
//    public String viewAccountDetail(@PathVariable Long id, Model model) {
//        Account account = accountService.getAccountById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy account"));
//        model.addAttribute("account", account);
//        return "account-detail"; // tên file account-detail.html
//    }
}
