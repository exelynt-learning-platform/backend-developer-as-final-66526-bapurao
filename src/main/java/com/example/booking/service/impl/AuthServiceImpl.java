package com.example.booking.service.impl;

import com.example.booking.dto.auth.LoginRequest;
import com.example.booking.dto.auth.LoginResponse;
import com.example.booking.security.JwtService;
import com.example.booking.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager auth;
    private final JwtService jwt;

    @Override
    public LoginResponse login(LoginRequest request) {

        var authentication = auth.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token = jwt.generateToken(userDetails);

        return new LoginResponse(
                token,
                "Bearer",
                jwt.getExpiration() / 1000
        );
    }
}