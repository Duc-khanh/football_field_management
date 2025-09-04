package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.service.Admin.users.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class ListUserAccount {

    private final AccountService accountService;
    private final com.example.football_field_management.repository.RoleRepository roleRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

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
    public String addAccount(@ModelAttribute Account account,
                             @RequestParam("avatarFile") MultipartFile avatarFile) throws IOException {
        account.setStatus(true);

        if (!avatarFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.createDirectories(path.getParent());
            avatarFile.transferTo(path.toFile());

            account.setAvt_path(fileName);
        }

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
        return "admin/account/edit-account";
    }

    @PostMapping("/edit/{id}")
    public String editAccount(@PathVariable Long id,
                              @ModelAttribute Account account,
                              @RequestParam("avatarFile") MultipartFile avatarFile) throws IOException {
        Account existing = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Giữ nguyên role
        account.setRoles(existing.getRoles());

        // Giữ password cũ nếu không đổi
        if (account.getPassword() == null || account.getPassword().isBlank()) {
            account.setPassword(existing.getPassword());
        }

        // Nếu có upload ảnh mới → xóa ảnh cũ rồi thay
        if (!avatarFile.isEmpty()) {
            // Xóa ảnh cũ (nếu có)
            if (existing.getAvt_path() != null) {
                Path oldPath = Paths.get(uploadDir,
                        existing.getAvt_path().replace("/uploads/avatars/", ""));
                File oldFile = oldPath.toFile();
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            // Lưu ảnh mới
            String fileName = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.createDirectories(path.getParent());
            avatarFile.transferTo(path.toFile());

// 👉 chỉ lưu tên file
            account.setAvt_path(fileName);

        } else {
            account.setAvt_path(existing.getAvt_path()); // giữ ảnh cũ
        }

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

    @GetMapping("/toggle/{id}")
    public String toggleAccountStatus(@PathVariable Long id) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus(!Boolean.TRUE.equals(account.getStatus()));

        accountService.save(account);
        return "redirect:/accounts";
    }
}
