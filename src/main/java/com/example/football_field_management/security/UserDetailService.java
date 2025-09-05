package com.example.football_field_management.security;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final AccountRepository accountRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account;

        if (username.contains("@")) {
            account = accountRepo.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + username));
        } else {
            account = accountRepo.findByPhone(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Phone number not found: " + username));
        }

        if (Boolean.FALSE.equals(account.getStatus())) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa, vui lòng liên hệ admin");
        }

        List<SimpleGrantedAuthority> authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole_name()))
                .toList();

        return new User(
                account.getEmail() != null ? account.getEmail() : account.getPhone(),
                account.getPassword(),
                authorities
        );
    }
}
