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
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(userRequest);

        // Copy attributes để đảm bảo ta có thể sửa
        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());

        // Lấy email từ Google
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null) {
            throw new RuntimeException("Google không trả về email. Hãy bật scope email!");
        }

        // Ép attribute email (đảm bảo luôn tồn tại cho SuccessHandler)
        attributes.put("email", email);

        // Kiểm tra user trong DB
        Account account = accountRepository.findByEmail(email).orElse(null);

        if (account == null) {

            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_USER chưa tồn tại trong DB!"));

            account = new Account();
            account.setEmail(email);
            account.setFullName(name != null ? name : "Google User");
            account.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            account.setProvider("GOOGLE");
            account.setStatus(true);
            account.setRoles(Set.of(userRole));

            accountRepository.save(account);
            System.out.println("🆕 Tạo mới tài khoản Google: " + email);

        } else {
            // Cập nhật tên nếu Google thay đổi
            if (name != null && !name.equals(account.getFullName())) {
                account.setFullName(name);
                accountRepository.save(account);
            }
            System.out.println("🔑 Đăng nhập Google: " + email);
        }

        // Tạo quyền
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        account.getRoles().forEach(r ->
                authorities.add(new SimpleGrantedAuthority(r.getRoleName()))
        );

        // Trả về OAuth2User (key attribute = "email")
        return new DefaultOAuth2User(
                authorities,
                attributes,
                "email"
        );
    }
}
