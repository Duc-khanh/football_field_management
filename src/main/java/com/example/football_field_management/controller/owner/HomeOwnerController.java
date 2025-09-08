package com.example.football_field_management.controller.owner;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
public class HomeOwnerController {
    @GetMapping("/homeOwner")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities on /users/homeOwner: " + auth.getAuthorities());
        model.addAttribute("username", auth.getName());
        return "owner/home";
    }
}
