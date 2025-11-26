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
public class  UserDetailService implements UserDetailsService {

    private final AccountRepository accountRepo;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Account acc = accountRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));

        return new CustomUserDetails(acc);
    }

}
