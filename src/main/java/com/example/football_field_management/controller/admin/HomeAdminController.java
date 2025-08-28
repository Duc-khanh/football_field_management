package com.example.football_field_management.controller.admin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/admin")
public class HomeAdminController {
    @GetMapping("/homeAdmin")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities on /admin/homeAdmin: " + auth.getAuthorities());
        model.addAttribute("username", auth.getName());
        return "admin/home";
    }


}
