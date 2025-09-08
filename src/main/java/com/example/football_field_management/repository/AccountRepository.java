package com.example.football_field_management.repository;


import com.example.football_field_management.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByPhone(String phone);
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);


    List<Account> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String keyword, String keyword1);
    Page<Account> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullName, String email, Pageable pageable);
    Page<Account> findByFullNameContainingOrEmailContaining(String name, String email, Pageable pageable);



}

