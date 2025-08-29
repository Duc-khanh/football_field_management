package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.repository.RoleRepository;
import com.example.football_field_management.service.User.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class ListUserAccount {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    @GetMapping
    public String viewAccounts(Model model) {
        model.addAttribute("accounts", accountService.getAllAccounts());
        return "admin/account/account-list";
    }

    @GetMapping("/{id}")
    public String viewAccountDetail(@PathVariable Long id, Model model) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account"));
        model.addAttribute("account", account);
        return "admin/account/account-detail";
    }

    @GetMapping("/add")
    public String addAccountForm(Model model) {
        model.addAttribute("account", new Account());
        return "admin/account/add-account";
    }

    @PostMapping("/add")
    public String addAccount(@ModelAttribute Account account) {
        // Gán mặc định ROLE_USER
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        account.setRoles(new HashSet<>(List.of(userRole)));

        accountService.save(account);
        return "redirect:/accounts";
    }

    @GetMapping("/edit/{id}")
    public String editAccountForm(@PathVariable Long id, Model model) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        model.addAttribute("account", account);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/account/edit-account";
    }

    @PostMapping("/edit/{id}")
    public String editAccount(@PathVariable Long id,
                              @ModelAttribute Account account) {
        Account existing = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Giữ nguyên role cũ khi edit
        account.setRoles(existing.getRoles());

        accountService.save(account);
        return "redirect:/accounts";
    }
    @GetMapping("/search")
    public String searchAccounts(@RequestParam("keyword") String keyword, Model model) {
        List<Account> accounts = accountService.searchAccounts(keyword);
        model.addAttribute("accounts", accounts);
        model.addAttribute("keyword", keyword);
        return "admin/account/account-list";
    }

}
