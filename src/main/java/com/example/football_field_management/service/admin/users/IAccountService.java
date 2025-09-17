package com.example.football_field_management.service.admin.users;

import com.example.football_field_management.dto.AuthResponse;
import com.example.football_field_management.dto.LoginRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.service.IGeneraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAccountService extends IGeneraService<Account> {
    AuthResponse login(LoginRequest loginRequest);
    Page<Account> getAccountsPaginated(Pageable pageable);

    Page<Account> searchAccounts(String keyword, Pageable pageable);
    Account findByEmail(String email);

    boolean existsByEmail(String email);

    Account register(Account account);

//    public interface IAccountService {
//        void register(Account account);
//    }

}
