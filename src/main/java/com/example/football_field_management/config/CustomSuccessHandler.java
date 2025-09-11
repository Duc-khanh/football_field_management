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

        var authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/dashboard";
                break;
            } else if (role.equals("ROLE_OWNER")) {
                redirectUrl = "/owner/homeOwner";
                break;
            } else if (role.equals("ROLE_USER")) {
                redirectUrl = "/";   // luôn vào trang chủ user
                break;
            }
        }
        response.sendRedirect(redirectUrl);
    }
}


