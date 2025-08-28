package com.example.football_field_management.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/admin")
public class HomeAdminController {
    @GetMapping("/homeAdmin")
    public String home(){
        return "admin/home";
    }

}
