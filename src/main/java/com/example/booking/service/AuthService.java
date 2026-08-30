package com.example.booking.service;

import com.example.booking.dto.auth.*;

public interface AuthService {
	LoginResponse login(LoginRequest request);
}
