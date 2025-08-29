package com.example.football_field_management.service.User;

import com.example.football_field_management.dto.AuthResponse;
import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.util.JwtUtil;
import lombok.AllArgsConstructor;
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
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id.intValue());
    }

    @Override
    public void save(Account account) {
        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        accountRepository.save(account);
    }

    @Override
    public void remote(Long id) {
        accountRepository.deleteById(id.intValue());
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
                .map(Role::getRole_name)
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

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id.intValue());
    }
    public List<Account> searchAccounts(String keyword) {
        return accountRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

}
