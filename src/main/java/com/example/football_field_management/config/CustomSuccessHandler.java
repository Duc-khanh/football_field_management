package com.example.football_field_management.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String redirectUrl = "/";

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                request.getSession().setAttribute("successMessage", "Đăng nhập trang quản trị thành công!");
                redirectUrl = "/dashboard";
                break;
            } else if ("ROLE_OWNER".equals(role)) {
                redirectUrl = "/dashboard";
                break;
            } else if ("ROLE_USER".equals(role)) {
                redirectUrl = "/";
                break;
            }
        }
        response.sendRedirect(redirectUrl);
    }
}


