package com.example.football_field_management.controller.users;

import com.example.football_field_management.dto.AuthResponse;
import com.example.football_field_management.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.GrantedAuthority;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        String avatarUrl = user.getAccount().getAvt_path();
        if (avatarUrl != null && !avatarUrl.startsWith("http")) {
            avatarUrl = "/uploads/avatars/" + avatarUrl;
        }

        AuthResponse authResponse = AuthResponse.builder()
                .fullName(user.getFullName())
                .email(user.getAccount().getEmail())
                .phone(user.getAccount().getPhone())
                .address(user.getAccount().getAddress())
                .avatarUrl(avatarUrl)
                .status(user.getAccount().getStatus() ? "active" : "blocked")
                .roles(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build();

        return ResponseEntity.ok(authResponse);
    }
}
