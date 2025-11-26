package com.example.football_field_management.service;



import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.PasswordResetToken;
import com.example.football_field_management.repository.PasswordResetTokenRepository;
import com.example.football_field_management.service.admin.users.IAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final IAccountService accountService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    // Tạo token mới và lưu vào DB
    public String createPasswordResetToken(Account account) {
        // Xóa token cũ nếu có
        passwordResetTokenRepository.findByAccount(account).ifPresent(passwordResetTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setAccount(account);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        passwordResetTokenRepository.save(resetToken);
        return token;
    }


    // Kiểm tra token còn hợp lệ không
    public boolean isPasswordResetTokenValid(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    // Cập nhật mật khẩu và xóa token
    public void updatePassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        Account account = resetToken.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountService.save(account); // Cần có phương thức save trong accountService
        tokenRepository.delete(resetToken);
    }

    public void updateLastEmailSentTime(Account account) {
        PasswordResetToken token = passwordResetTokenRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Mã không tồn tại"));

        token.setLastPasswordResetEmailSentAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);
    }

    public PasswordResetToken getTokenByAccount(Account account) {
        return passwordResetTokenRepository.findByAccount(account).orElse(null);
    }
}

