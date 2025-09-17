package com.example.football_field_management.controller.authenticate;

import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.service.admin.users.IAccountService;
import com.example.football_field_management.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final IAccountService accountService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequest loginRequest) {
        Account acc = accountService.findByEmail(loginRequest.getEmail());

        if (acc == null) {
            return ResponseEntity.badRequest().body("Tài khoản không tồn tại");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), acc.getPassword())) {
            return ResponseEntity.badRequest().body("Sai mật khẩu");
        }

        // Lấy danh sách role name
        List<String> roles = acc.getRoles().stream()
                .map(r -> r.getRole_name())
                .toList();

        // Tạo token JWT
        String token = jwtUtil.generateToken(acc.getEmail(), roles);

        // Trả về token + info cơ bản
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", acc.getEmail(),
                "fullName", acc.getFullName(),
                "roles", roles
        ));
    }
}

