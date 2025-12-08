package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.service.admin.users.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
public String listAccounts(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String role) {

    Page<Account> accountPage;

    if (role != null && !role.isBlank()) {
        accountPage = accountService.getAccountsByRole(role, page, size, keyword);
    } else {
        accountPage = accountService.getAccounts(page, size, keyword);
    }

    model.addAttribute("accounts", accountPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", accountPage.getTotalPages());
    model.addAttribute("pageSize", size);
    model.addAttribute("keyword", keyword);
    model.addAttribute("role", role);

    model.addAttribute("roles", roleRepository.findAll());

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
        return "admin/account/account-add";
    }

    @GetMapping("/edit/{id}")
    public String editAccountForm(@PathVariable Long id, Model model) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        model.addAttribute("account", account);
        return "admin/account/account-edit";
    }
    @PostMapping("/add")
    public String addAccount(@ModelAttribute Account account,
                             @RequestParam("avatarFile") MultipartFile avatarFile,
                             RedirectAttributes redirectAttributes) throws IOException {
        account.setStatus(true);

        // Kiểm tra email trùng
        if (accountService.existsByEmail(account.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email này đã tồn tại!");
            redirectAttributes.addFlashAttribute("account", account);
            return "redirect:/admin/account/add"; // redirect về form thêm
        }

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
        redirectAttributes.addFlashAttribute("successMessage", "Thêm tài khoản thành công!");
        return "redirect:/accounts";
    }
    @PostMapping("/edit/{id}")
    public String editAccount(@PathVariable Long id,
                              @ModelAttribute Account account,
                              @RequestParam("avatarFile") MultipartFile avatarFile,
                              RedirectAttributes redirectAttributes) throws IOException {
        Account existing = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Check nếu đổi email sang cái đã tồn tại
        if (!existing.getEmail().equals(account.getEmail())
                && accountService.existsByEmail(account.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email này đã tồn tại!");
            redirectAttributes.addFlashAttribute("account", existing);
            return "redirect:/admin/account/edit/" + id;
        }

        account.setRoles(existing.getRoles());

        if (account.getPassword() == null || account.getPassword().isBlank()) {
            account.setPassword(existing.getPassword());
        }

        if (!avatarFile.isEmpty()) {
            if (existing.getAvt_path() != null) {
                Path oldPath = Paths.get(uploadDir,
                        existing.getAvt_path().replace("/uploads/avatars/", ""));
                File oldFile = oldPath.toFile();
                if (oldFile.exists()) oldFile.delete();
            }

            String fileName = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.createDirectories(path.getParent());
            avatarFile.transferTo(path.toFile());
            account.setAvt_path(fileName);
        } else {
            account.setAvt_path(existing.getAvt_path());
        }

        accountService.save(account);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tài khoản thành công!");
        return "redirect:/accounts";
    }





    @GetMapping("/search")
public String searchAccounts(@RequestParam("keyword") String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
    int pageSize = 10;
    Page<Account> accountPage = accountService.searchAccounts(keyword, PageRequest.of(page, pageSize));

    model.addAttribute("accounts", accountPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", accountPage.getTotalPages());
    model.addAttribute("keyword", keyword);

    return "admin/account/account-list";
}

    @GetMapping("/toggle/{id}")
    public String toggleAccountStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        boolean newStatus = !Boolean.TRUE.equals(account.getStatus());
        account.setStatus(newStatus);
        accountService.save(account);

        String statusText = newStatus ? "mở" : "khoá";
        redirectAttributes.addFlashAttribute("successMessage",
                "Tài khoản '" + account.getFullName() + "' đã được " + statusText + " thành công!");

        return "redirect:/accounts";
    }

}
