package com.example.football_field_management.config;

import com.example.football_field_management.security.CustomUserDetails;
import com.example.football_field_management.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();
        String email;

        // ===== FORM LOGIN =====
        if (principal instanceof CustomUserDetails customUser) {
            email = customUser.getEmail();
        }
        // ===== GOOGLE OAUTH2 =====
        else if (principal instanceof OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");  // đảm bảo service trả về email
        }
        // ===== SPRING INTERNAL =====
        else if (principal instanceof User springUser) {
            email = springUser.getUsername();
        }
        else {
            email = "unknown@example.com";
        }

        // Lấy role
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Tạo JWT
        String token = jwtUtil.generateToken(email, roles);

        // redirect theo role
        String redirectUrl = switch (roles.get(0)) {
            case "ROLE_ADMIN" -> "/dashboard";
            case "ROLE_OWNER" -> "/owner/dashboard";
            default ->
                    "http://localhost:3000/login-success?token=" + token + "&email=" + email;
        };

        response.sendRedirect(redirectUrl);
    }
}
