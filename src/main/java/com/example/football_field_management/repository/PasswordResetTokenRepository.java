package com.example.football_field_management.repository;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByAccount(Account account);
}
