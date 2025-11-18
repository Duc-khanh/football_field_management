package com.example.football_field_management.controller.authenticate;

import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.repository.RoleRepository;
import com.example.football_field_management.security.JwtTokenProvider;
import com.example.football_field_management.service.admin.users.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:3000")
@RestController("usersAuthController")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAccountService accountService;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Lấy account theo email
        Account account = accountService.findByEmail(request.getEmail());

        if (account == null) {
            // Account không tồn tại
            return ResponseEntity.status(401).body(Map.of("error", "Email hoặc mật khẩu không đúng!"));
        }

        if (!account.getStatus()) {
            // Account bị khóa
            return ResponseEntity.status(403).body(Map.of("error", "Tài khoản đã bị khóa!"));
        }

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            // Password sai
            return ResponseEntity.status(401).body(Map.of("error", "Email hoặc mật khẩu không đúng!"));
        }

        // Tạo JWT token
        String token = jwtTokenProvider.generateToken(account.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", account.getEmail(),
                "role", account.getRoles().iterator().next().getRoleName()
        ));
    }



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        if (accountService.existsByEmail(account.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email đã tồn tại!"));
        }

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRoles(new HashSet<>(List.of(userRole)));

        accountService.register(account);
        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Nếu dùng JWT thì logout phía client chỉ cần xoá token
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công!"));
    }
}
