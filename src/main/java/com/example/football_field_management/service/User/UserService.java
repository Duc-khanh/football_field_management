package com.example.football_field_management.service.User;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public Iterable<Account> findAll() {
        return userRepository.findAll();
    }
    @Override
    public Optional<Account> findById(Long id) {
        return userRepository.findById(id);
    }
    @Override
    public void save(Account account) {
        userRepository.save(account);
    }
    @Override
    public void remote(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<Account> findByEmail(String email, String password) {
        Optional<Account> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String roleName = account.getRole().getName();
        return org.springframework.security.core.userdetails.User.builder()
                .username(account.getEmail())
                .password(account.getPassword())
                .authorities(new SimpleGrantedAuthority(roleName))
                .build();
    }
}
