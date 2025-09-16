package com.example.football_field_management.controller.authenticate;


import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.repository.RoleRepository;
import com.example.football_field_management.service.admin.users.IAccountService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthControllerT {
    private final IAccountService accountService;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Bạn đã đăng xuất thành công!");
        return "redirect:/auth/login";
    }


    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("account", new Account());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerAccount(@ModelAttribute("account") Account account,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        if (!account.getPassword().equals(account.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "auth/register";
        }

        if (accountService.existsByEmail(account.getEmail())) {
            model.addAttribute("errorMessage", "Email này đã tồn tại!");
            return "auth/register";
        }

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
        account.setRoles(new HashSet<>(List.of(userRole)));


        accountService.register(account);
        model.addAttribute("successMessage", "Đăng ký thành công, mời bạn đăng nhập!");
        return "redirect:/auth/login";
    }
}
