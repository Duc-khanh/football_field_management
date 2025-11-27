package com.example.football_field_management.repository;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByPhone(String phone);

    Page<Account> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullName, String email, Pageable pageable);

    @Query("SELECT a FROM Account a JOIN a.roles r WHERE r.roleName = :roleName")
    Page<Account> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    @Query("SELECT a FROM Account a JOIN a.roles r " +
            "WHERE r.roleName = :roleName " +
            "AND (LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Account> findByRoleNameAndKeyword(@Param("roleName") String roleName,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

    boolean existsByEmail(String email);


    @Query("SELECT a FROM Account a JOIN a.roles r " +
            "WHERE r.roleName = :roleName AND a.approvalStatus = :status " +
            "ORDER BY a.account_id DESC")
    Page<Account> findOwnersByRoleAndStatus(@Param("roleName") String roleName,
                                            @Param("status") ApprovalStatus status,
                                            Pageable pageable);


}
