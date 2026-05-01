package com.agriconnect.auth.service;

import com.agriconnect.auth.dto.request.LoginRequest;
import com.agriconnect.auth.dto.request.RegisterRequest;
import com.agriconnect.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(java.util.UUID userId, String refreshToken);

    AuthResponse refreshToken(String rawRefreshToken);
}
