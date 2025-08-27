package com.example.football_field_management.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
public class HomeController {
    @GetMapping("/test")
    public String testApi() {
        return "✅ API connected successfully!";
    }
}
