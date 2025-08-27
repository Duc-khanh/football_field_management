package com.example.football_field_management.service.User;

import com.example.football_field_management.dto.AuthResponse;
import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.service.IGeneraService;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface IAccountService extends IGeneraService<Account> {
    AuthResponse login(LoginRequest loginRequest);
}
