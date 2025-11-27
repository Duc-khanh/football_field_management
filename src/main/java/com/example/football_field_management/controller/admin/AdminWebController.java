package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.ApprovalStatus;
import com.example.football_field_management.service.admin.users.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @Autowired
    private AccountService accountService;

    // Hiển thị danh sách OWNER chờ duyệt với phân trang + tìm kiếm
    @GetMapping("/browseAccount")
    public String showDashboard(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword
    ) {
        Page<Account> ownersPage = accountService.getPendingOwners(page, size, keyword);
        model.addAttribute("ownersPage", ownersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ownersPage.getTotalPages());
        model.addAttribute("keyword", keyword == null ? "" : keyword);

        return "/admin/browseAccount";
    }

    // Duyệt hoặc từ chối OWNER
    @GetMapping("/approve/{id}")
    public String approveOwner(
            @PathVariable Long id,
            @RequestParam ApprovalStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword
    ) {
        accountService.updateOwnerStatus(id, status);

        // redirect về trang hiện tại + keyword
        if (keyword == null) keyword = "";
        return "redirect:/admin/browseAccount?page=" + page + "&keyword=" + keyword;
    }
}
