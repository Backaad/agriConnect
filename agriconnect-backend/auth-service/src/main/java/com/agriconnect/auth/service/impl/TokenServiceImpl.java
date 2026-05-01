package com.agriconnect.auth.service.impl;

import com.agriconnect.auth.domain.entity.RefreshToken;
import com.agriconnect.auth.exception.AuthException;
import com.agriconnect.auth.repository.RefreshTokenRepository;
import com.agriconnect.auth.security.JwtTokenProvider;
import com.agriconnect.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String createRefreshToken(UUID userId) {
        String rawToken = jwtTokenProvider.generateRefreshToken(userId);
        String hash = passwordEncoder.encode(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hash)
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String rawToken) {
        UUID userId = jwtTokenProvider.getUserIdFromToken(rawToken);

        return refreshTokenRepository.findAll().stream()
                .filter(t -> t.getUserId().equals(userId)
                          && !t.isRevoked()
                          && !t.isExpired()
                          && passwordEncoder.matches(rawToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new AuthException("Refresh token invalide ou expiré", "INVALID_REFRESH_TOKEN"));
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String rawToken) {
        try {
            UUID userId = jwtTokenProvider.getUserIdFromToken(rawToken);
            refreshTokenRepository.findAll().stream()
                    .filter(t -> t.getUserId().equals(userId)
                              && passwordEncoder.matches(rawToken, t.getTokenHash()))
                    .findFirst()
                    .ifPresent(t -> {
                        t.setRevoked(true);
                        refreshTokenRepository.save(t);
                    });
        } catch (Exception e) {
            log.warn("Impossible de révoquer le refresh token: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
        log.info("Tous les refresh tokens révoqués pour userId={}", userId);
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Chaque nuit à 2h
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime cutoff = LocalDateTime.now();
        refreshTokenRepository.deleteExpiredAndRevoked(cutoff);
        log.info("Nettoyage des tokens expirés effectué");
    }
}
