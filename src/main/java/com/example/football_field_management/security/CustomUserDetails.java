package com.example.football_field_management.security;

import com.example.football_field_management.model.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Account account;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Account account) {
        this.account = account;

        this.authorities = List.of(
                new SimpleGrantedAuthority(account.getRoles().iterator().next().getRoleName())
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        // Bạn có thể dùng email hoặc phone làm username
        return account.getEmail() != null ? account.getEmail() : account.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(account.getStatus());
    }

    // ==== Getter bổ sung cho thông tin người dùng ====
    public String getFullName() { return account.getFullName(); }
    public String getEmail()    { return account.getEmail(); }
    public String getPhone()    { return account.getPhone(); }
    public String getAddress()  { return account.getAddress(); }
    public String getAvatarUrl(){ return account.getAvt_path(); }
    public String getStatus()   { return account.getStatus() ? "active" : "block"; }
}
