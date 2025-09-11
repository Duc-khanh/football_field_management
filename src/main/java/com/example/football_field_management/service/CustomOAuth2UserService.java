package com.example.football_field_management.service;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println("Google attributes: " + attributes);

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null) {
            throw new RuntimeException("Google không trả về email, hãy kiểm tra scope trong application.properties");
        }

        Account account = accountRepository.findByEmail(email).orElse(null);

        if (account == null) {
            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_USER chưa tồn tại"));

            account = new Account();
            account.setEmail(email);
            account.setFullName(name != null ? name : "Google User");
            account.setPassword(UUID.randomUUID().toString());

            // Gán ROLE_USER vào account
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            account.setRoles(roles);

            accountRepository.save(account);
            System.out.println("Tạo mới account thành công: " + account.getEmail());
        } else {
            System.out.println("Đã tồn tại account: " + account.getEmail());
        }

        // Convert Role -> SimpleGrantedAuthority
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        account.getRoles().forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role.getRole_name()))
        );

        return new DefaultOAuth2User(
                authorities,
                attributes,
                "email" // Dùng email làm key chính
        );
    }
}
