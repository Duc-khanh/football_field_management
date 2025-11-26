package com.example.football_field_management.controller.admin;



import com.example.football_field_management.dto.AccountResponse;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    @GetMapping("/me")
    public AccountResponse getCurrentUser(Authentication authentication) {

        String email = authentication.getName(); // lấy email từ JWT

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AccountResponse res = new AccountResponse();
        res.setId(account.getAccount_id());
        res.setFullName(account.getFullName());
        res.setEmail(account.getEmail());
        res.setPhone(account.getPhone());
        res.setAddress(account.getAddress());
        res.setAvatar(account.getAvt_path());
        res.setProvider(account.getProvider());
        res.setStatus(account.getStatus());

        // Lấy role đầu tiên (vì bạn chỉ dùng 1 role/user)
        res.setRole(account.getRoles()
                .stream()
                .findFirst()
                .map(r -> r.getRoleName())
                .orElse(null)
        );

        return res;
    }
}
