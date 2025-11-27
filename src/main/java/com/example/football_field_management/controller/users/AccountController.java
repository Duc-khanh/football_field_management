package com.example.football_field_management.controller.users;

import com.example.football_field_management.dto.AccountResponse;
import com.example.football_field_management.dto.UpdateProfileRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    @GetMapping("/me")
    public AccountResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

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
        res.setRole(account.getRoles()
                .stream()
                .findFirst()
                .map(r -> r.getRoleName())
                .orElse(null)
        );

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<AccountResponse> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest updatedProfile
    ) {
        String email = authentication.getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatedProfile.getFullName() != null) account.setFullName(updatedProfile.getFullName());
        if (updatedProfile.getPhone() != null) account.setPhone(updatedProfile.getPhone());
        if (updatedProfile.getAddress() != null) account.setAddress(updatedProfile.getAddress());
        if (updatedProfile.getAvatar() != null && !updatedProfile.getAvatar().isEmpty()) {
            account.setAvt_path(updatedProfile.getAvatar());
        }

        Account saved = accountRepository.save(account);

        AccountResponse res = new AccountResponse();
        res.setId(saved.getAccount_id());
        res.setFullName(saved.getFullName());
        res.setEmail(saved.getEmail());
        res.setPhone(saved.getPhone());
        res.setAddress(saved.getAddress());
        res.setAvatar(saved.getAvt_path());
        res.setProvider(saved.getProvider());
        res.setStatus(saved.getStatus());
        res.setRole(saved.getRoles().stream().findFirst().map(r -> r.getRoleName()).orElse(null));

        return ResponseEntity.ok(res);
    }


}
