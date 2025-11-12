package com.example.football_field_management.controller.authenticate;


import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.repository.RoleRepository;
import com.example.football_field_management.service.admin.users.IAccountService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController("restAuthController")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final IAccountService accountService;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@RequestBody Account account) {
        Map<String, Object> response = new HashMap<>();

        if (!account.getPassword().equals(account.getConfirmPassword())) {
            response.put("error", "Mật khẩu xác nhận không khớp!");
            return ResponseEntity.badRequest().body(response);
        }

        if (accountService.existsByEmail(account.getEmail())) {
            response.put("error", "Email này đã tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
        account.setRoles(new HashSet<>(List.of(userRole)));

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        accountService.register(account);
        response.put("message", "Đăng ký thành công!");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Account account = accountService.findByEmail(loginRequest.getEmail());
        if (account == null) {
            response.put("error", "Email không tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())) {
            response.put("error", "Sai mật khẩu!");
            return ResponseEntity.badRequest().body(response);
        }


        session.setAttribute("user", account);
        response.put("message", "Đăng nhập thành công!");
        response.put("account", account);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng xuất thành công!");
        return ResponseEntity.ok(response);
    }
}

