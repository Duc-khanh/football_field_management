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

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        // Lấy thông tin người dùng từ Google
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null) {
            throw new RuntimeException(
                    "Google không trả về email. Kiểm tra scope trong cấu hình: cần 'openid, profile, email'"
            );
        }

        // Tìm account trong DB theo email
        Account account = accountRepository.findByEmail(email).orElse(null);

        if (account == null) {
            // Lấy role ROLE_USER từ DB
            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_USER chưa tồn tại trong DB"));

            // Tạo mới account
            account = new Account();
            account.setEmail(email);
            account.setFullName(name != null ? name : "Google User");
            account.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // random pass

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            account.setRoles(roles);

            accountRepository.save(account);
            System.out.println("Tạo mới account: " + email);
        } else {
            System.out.println("Đăng nhập bằng Google với account: " + email);
        }

        // Convert Role -> GrantedAuthority
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        account.getRoles().forEach(r ->
                authorities.add(new SimpleGrantedAuthority(r.getRole_name()))
        );

        // Trả về user cho Spring Security
        return new DefaultOAuth2User(
                authorities,
                attributes,
                "email"   // attribute chính để định danh
        );
    }
}
