package com.example.football_field_management.service;

import com.example.football_field_management.model.Account;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface IAccountService {
    Account registerAccount(Account account, MultipartFile file) throws IOException;

    Optional<Account> login(String email, String password);

    Optional<Account> getAccountByEmail(String email);

    Account updateAvatar(Integer account_id, MultipartFile file) throws IOException;
}
