package com.example.football_field_management.service.User;

import com.example.football_field_management.dto.AuthResponse;
import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Role;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class AccountService implements IAccountService {


    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;


    @Override
    public Iterable<Account> findAll() {
        return null;
    }

    @Override
    public Optional<Account> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void save(Account account) {

    }

    @Override
    public void remote(Long id) {

    }


    @Override
    public AuthResponse login(LoginRequest request) {
        // Xác thực username/password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        // Tạo JWT token
        String token = jwtUtil.generateToken(authentication);

        // Lấy account từ DB
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Lấy danh sách role từ account
        Set<String> roles = account.getRoles().stream()
                .map(Role::getRole_name)   // giả sử field trong Role là roleName
                .collect(Collectors.toSet());

        // Build response
        AuthResponse response = new AuthResponse();
        response.setFullName(account.getFull_name());
        response.setEmail(account.getEmail());
        response.setPhone(account.getPhone());
        response.setAddress(account.getAddress());
        response.setStatus(account.getStatus());
        response.setToken(token);
        response.setRoles(roles);   // thêm roles vào response

        return response;
    }

}
