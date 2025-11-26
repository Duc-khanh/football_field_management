package com.example.football_field_management.security;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.ApprovalStatus;
import com.example.football_field_management.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Tìm tài khoản
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy tài khoản với email: " + email
                ));

        // 2. Kiểm tra có phải OWNER không
        boolean isOwner = account.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("ROLE_OWNER"));

        if (isOwner) {

            ApprovalStatus status = account.getApprovalStatus();

            // Nếu chưa duyệt
            if (status == ApprovalStatus.PENDING) {
                throw new DisabledException("Tài khoản của bạn đang chờ Admin phê duyệt.");
            }

            // Nếu bị từ chối
            if (status == ApprovalStatus.REJECTED) {
                throw new LockedException("Tài khoản của bạn đã bị từ chối.");
            }
        }

        // 3. Trả về User cho Spring Security
        return new User(
                account.getEmail(),
                account.getPassword(),
                account.getStatus(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked (bị khóa bằng status ở trên)
                account.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                        .collect(Collectors.toList())
        );
    }
}
