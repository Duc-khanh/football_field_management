package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin/account")
@RequiredArgsConstructor
public class AccountEditController {

    private final AccountRepository accountRepo;
    @Value("${file.upload-dir}")
    private String uploadDir;

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
            return "redirect:/login";
        }

        model.addAttribute("account", account);
        return "admin/account/profile";
    }
    @PostMapping
    public String saveProfile(Account account, MultipartFile avatarFile, RedirectAttributes redirectAttributes) {
        try {
            Account existingAccount = accountRepo.findById(account.getAccount_id()).orElse(null);
            if (existingAccount == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản không tồn tại!");
                return "redirect:/login";
            }

            // Cập nhật thông tin
            existingAccount.setFullName(account.getFullName());
            existingAccount.setEmail(account.getEmail());
            existingAccount.setPhone(account.getPhone());
            existingAccount.setAddress(account.getAddress());

            // Upload avatar
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(avatarFile.getOriginalFilename());
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                avatarFile.transferTo(filePath.toFile());
                existingAccount.setAvt_path(fileName);
            }

            accountRepo.save(existingAccount);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lưu hồ sơ thất bại: ");
        }
        return "redirect:/dashboard";
    }
}
