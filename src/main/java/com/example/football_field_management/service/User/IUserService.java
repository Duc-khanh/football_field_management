package com.example.football_field_management.service.User;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.service.IGeneraService;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface IUserService extends IGeneraService<Account> {
    Optional<Account> findByEmail(String email, String password);
    UserDetails loadUserByUsername(String email);
}
