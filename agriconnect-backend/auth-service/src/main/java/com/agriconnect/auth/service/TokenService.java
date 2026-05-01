package com.agriconnect.auth.service;

import com.agriconnect.auth.domain.entity.RefreshToken;
import com.agriconnect.auth.domain.entity.User;

import java.util.UUID;

public interface TokenService {

    String createRefreshToken(UUID userId);

    RefreshToken validateRefreshToken(String rawToken);

    void revokeRefreshToken(String rawToken);

    void revokeAllUserTokens(UUID userId);

    void cleanupExpiredTokens();
}
