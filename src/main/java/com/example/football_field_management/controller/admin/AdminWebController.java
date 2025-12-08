package com.example.football_field_management.controller.admin;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.ApprovalStatus;
import com.example.football_field_management.service.EmailService;
import com.example.football_field_management.service.admin.users.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final AccountService accountService;
    private final EmailService emailService;

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

    @GetMapping("/approve/{id}")
    public String approveOwner(
            @PathVariable Long id,
            @RequestParam ApprovalStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword
    ) {
        Optional<Account> owner = accountService.getAccountById(id);
        accountService.updateOwnerStatus(id, status);

        owner.ifPresent(acc -> {
            if (acc.getEmail() != null && !acc.getEmail().isEmpty()) {
                String subject = "Thông báo trạng thái tài khoản";
                String body = (status == ApprovalStatus.APPROVED)
                        ? "Xin chúc mừng! Tài khoản của bạn đã được duyệt."
                        : "Rất tiếc! Tài khoản của bạn không được duyệt.";

                emailService.sendSimpleMail(acc.getEmail(), subject, body);
            }
        });

        if (keyword == null) keyword = "";
        return "redirect:/admin/browseAccount?page=" + page + "&keyword=" + keyword;
    }

}
