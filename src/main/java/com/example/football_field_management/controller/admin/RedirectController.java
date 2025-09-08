package com.example.football_field_management.controller.admin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class RedirectController {

    @GetMapping("/default")
    public String redirectAfterLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/homeAdmin";
            } else if (role.equals("ROLE_OWNER")) {
                return "redirect:/owner/homeOwner";
            } else if (role.equals("ROLE_USER")) {
                return "redirect:/user/homeUser";
            }
        }
        return "redirect:/auth/login?error=true";
    }
}
