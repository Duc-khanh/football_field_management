package com.example.football_field_management.service.user;

import com.example.football_field_management.dto.AuthResponse;
import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.service.IGeneraService;

public interface IAccountService extends IGeneraService<Account> {
    AuthResponse login(LoginRequest loginRequest);
}
