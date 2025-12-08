package com.example.football_field_management.service;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(userRequest);

        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null) {
            throw new RuntimeException("Google không trả về email. Hãy bật scope email!");
        }

        // Kiểm tra account trong DB
        Account account = accountRepository.findByEmail(email).orElse(null);

        if (account == null) {
            // Lấy role ROLE_USER từ DB
            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_USER chưa tồn tại trong DB!"));

            // Tạo account mới
            account = new Account();
            account.setEmail(email);
            account.setFullName(name != null ? name : "Google User");
            account.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            account.setProvider("GOOGLE");
            account.setStatus(true);
            account.setRoles(new HashSet<>());
            account.getRoles().add(userRole);

            // Lưu account
            System.out.println("Saving account: " + account.getEmail());
            accountRepository.save(account);
            System.out.println("Saved account ID: " + account.getAccount_id());


            System.out.println("🆕 Tạo mới tài khoản Google: " + email);
        } else {
            // Cập nhật tên nếu khác
            if (name != null && !name.equals(account.getFullName())) {
                account.setFullName(name);
                accountRepository.save(account);
            }
            System.out.println("🔑 Đăng nhập Google: " + email);
        }

        // Chuyển role thành authorities
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        account.getRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getRoleName())));

        // Trả về DefaultOAuth2User, keyAttribute là email
        return new DefaultOAuth2User(
                authorities,
                attributes,
                "email"
        );
    }
}
