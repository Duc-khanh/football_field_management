package com.example.football_field_management.service.admin.users;

import com.example.football_field_management.dto.AuthResponse;
import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AccountService implements IAccountService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void save(Account account) {
        if (account.getAccount_id() != null) {
            Account existingAccount = accountRepository.findById(account.getAccount_id())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (account.getPassword() == null || account.getPassword().isEmpty()) {
                account.setPassword(existingAccount.getPassword());
            } else if (!account.getPassword().equals(existingAccount.getPassword())) {
                account.setPassword(passwordEncoder.encode(account.getPassword()));
            }
        } else {
            if (account.getPassword() != null && !account.getPassword().isEmpty()) {
                account.setPassword(passwordEncoder.encode(account.getPassword()));
            }
        }

        accountRepository.save(account);
    }

    public Account register(Account account) {
        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        return accountRepository.save(account);
    }

    @Override
    public Page<Account> getAccountsPaginated(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Page<Account> getAccounts(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            return accountRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        }
        return accountRepository.findAll(pageable);
    }

    @Override
    public Page<Account> searchAccounts(String keyword, Pageable pageable) {
        return accountRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Override
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public void remote(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(authentication);

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Set<String> roles = account.getRoles().stream()
                .map(Role::getRole_name) // vẫn dùng role_name trong entity
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .fullName(account.getFullName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .address(account.getAddress())
                .status(String.valueOf(account.getStatus()))
                .token(token)
                .roles(roles)
                .build();
    }

    public Page<Account> getAccountsByRole(String role_name, int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isBlank()) {
            return accountRepository.findByRoleNameAndKeyword(role_name, keyword, pageable);
        }
        return accountRepository.findByRoleName(role_name, pageable);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public boolean existsByEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElse(null);
    }

}
