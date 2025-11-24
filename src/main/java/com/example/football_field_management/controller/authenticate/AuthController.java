package com.example.football_field_management.controller.authenticate;

import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.dto.RegisterRequest;
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
import java.util.Set;

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

        // 1. Kiểm tra email
        Account account = accountService.findByEmail(request.getEmail());
        if (account == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Email hoặc mật khẩu không đúng!"));
        }

        // 2. Kiểm tra status
        if (!account.getStatus()) {
            return ResponseEntity.status(403).body(Map.of("error", "Tài khoản đã bị khóa!"));
        }

        // 3. Kiểm tra password
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Email hoặc mật khẩu không đúng!"));
        }

        // 4. Lấy role đầu tiên
        System.out.println("==== LOGIN DEBUG ====");
        System.out.println("Email login: " + request.getEmail());
        System.out.println("Roles of this account: ");

        for (Role r : account.getRoles()) {
            System.out.println(" - " + r.getRoleName());
        }

        String role = account.getRoles().iterator().next().getRoleName();
        System.out.println("Selected role from iterator(): " + role);


        // 5. CHỈ CHO PHÉP ROLE_USER
        if (!role.equals("ROLE_USER")) {
            return ResponseEntity.status(403).body(Map.of("error",
                    "Bạn không có quyền truy cập! Chỉ ROLE_USER mới được đăng nhập.abc"));
        }

        // 6. Tạo JWT token
        String token = jwtTokenProvider.generateToken(account.getEmail());

        // 7. Trả về thông tin account để lưu trên client
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", account.getEmail(),
                "role", role,
                "name", account.getFullName(),
                "id", account.getAccount_id()
        ));
    }



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            // Check email
            if (accountService.existsByEmail(req.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email đã tồn tại!"));
            }

            // Check confirmPassword
            if (!req.getPassword().equals(req.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu xác nhận không khớp!"));
            }

            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));

            Account account = Account.builder()
                    .fullName(req.getFullName())
                    .email(req.getEmail())
                    .phone(req.getPhone())
                    .password(passwordEncoder.encode(req.getPassword()))
                    .status(true)
                    .roles(Set.of(userRole))
                    .build();

            accountService.register(account);

            return ResponseEntity.ok(Map.of("message", "Đăng ký thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Đăng ký thất bại: " + e.getMessage()));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công!"));
    }
}
