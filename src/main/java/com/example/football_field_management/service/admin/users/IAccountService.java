package com.example.football_field_management.service.admin.users;

import com.example.football_field_management.dto.AuthResponse;
import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.dto.OwnerRegisterDTO;
import com.example.football_field_management.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IAccountService {
    Account save(Account account);
    void remote(Long id);
    Account register(Account account);

    Optional<Account> findById(Long id);
    List<Account> findAll();

    Page<Account> getAccountsPaginated(Pageable pageable);
    Page<Account> searchAccounts(String keyword, Pageable pageable);

    AuthResponse login(LoginRequest request);

    Page<Account> getAccountsByRole(String roleName, int page, int size, String keyword);
    List<Account> getAllAccounts();
    Optional<Account> getAccountById(Long id);

    boolean existsByEmail(String email);
    Account findByEmail(String email);

    Account registerOwner(OwnerRegisterDTO registerDTO);
}
