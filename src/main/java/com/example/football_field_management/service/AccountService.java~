package com.example.football_field_management.service;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Service
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Account registerAccount(Account account, MultipartFile file) throws IOException {
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + File.separator + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            account.setAvt_Path(fileName);
        }

        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> login(String email, String password) {
        return accountRepository.findByEmail(email)
                .filter(acc -> passwordEncoder.matches(password, acc.getPassword()));
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }




    @Override
    public Account updateAvatar(Integer account_id, MultipartFile file) throws IOException {
        Optional<Account> optionalAccount = accountRepository.findById(account_id);
        if (optionalAccount.isPresent() && file != null && !file.isEmpty()) {
            Account account = optionalAccount.get();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + File.separator + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            account.setAvt_Path(fileName);
            return accountRepository.save(account);
        }
        return null;
    }


}
