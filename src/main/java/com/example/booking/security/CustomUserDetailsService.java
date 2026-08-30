package com.example.booking.security;

import com.example.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return repo.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .authorities(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + u.getRole().name()
                                )
                        )
                        .build())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }
}