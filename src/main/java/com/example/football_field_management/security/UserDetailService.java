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

        // ✅ Thêm prefix ROLE_ để Spring Security hiểu đúng
        List<SimpleGrantedAuthority> authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole_name()))
                .toList();

        return new User(
                username,               // username or email/phone
                account.getPassword(),  // mật khẩu đã mã hoá
                authorities             // quyền như ROLE_ADMIN, ROLE_USER
        );
    }
}
