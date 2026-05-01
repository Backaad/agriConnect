package com.agriconnect.auth.service.impl;

import com.agriconnect.auth.domain.entity.User;
import com.agriconnect.auth.domain.enums.UserStatus;
import com.agriconnect.auth.dto.request.LoginRequest;
import com.agriconnect.auth.dto.request.RegisterRequest;
import com.agriconnect.auth.dto.response.AuthResponse;
import com.agriconnect.auth.event.model.UserRegisteredEvent;
import com.agriconnect.auth.event.publisher.AuthEventPublisher;
import com.agriconnect.auth.exception.AuthException;
import com.agriconnect.auth.repository.UserRepository;
import com.agriconnect.auth.security.JwtTokenProvider;
import com.agriconnect.auth.service.AuthService;
import com.agriconnect.auth.service.TokenService;
import com.agriconnect.commons.exception.ConflictException;
import com.agriconnect.commons.util.PhoneUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final AuthEventPublisher eventPublisher;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedPhone = PhoneUtils.normalize(request.getPhone());

        if (userRepository.existsByPhone(normalizedPhone)) {
            throw new ConflictException("Un compte existe déjà avec ce numéro de téléphone");
        }

        String role = request.getRole() != null ? request.getRole().toUpperCase() : "CONSUMER";
        if (!Set.of("FARMER", "WORKER", "CONSUMER").contains(role)) {
            throw new AuthException("Rôle invalide: " + role, "INVALID_ROLE");
        }

        User user = User.builder()
                .phone(normalizedPhone)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.PENDING)
                .build();
        user.addRole(role);
        user = userRepository.save(user);

        log.info("Nouvel utilisateur inscrit: userId={}, phone={}", user.getId(), PhoneUtils.mask(normalizedPhone));

        eventPublisher.publishUserRegistered(UserRegisteredEvent.builder()
                .userId(user.getId())
                .phone(normalizedPhone)
                .role(role)
                .source("MOBILE")
                .build());

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getPhone(), user.getRoles(), "PENDING");
        String refreshToken = tokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .phone(PhoneUtils.mask(normalizedPhone))
                .roles(user.getRoles())
                .kycStatus("PENDING")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpiryMs() / 1000)
                .otpVerified(false)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedPhone = PhoneUtils.normalize(request.getPhone());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedPhone, request.getPassword()));

        User user = userRepository.findByPhone(normalizedPhone)
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new AuthException("Votre compte est suspendu. Contactez le support.", "ACCOUNT_SUSPENDED");
        }
        if (user.getStatus() == UserStatus.DELETED) {
            throw new AuthException("Ce compte n'existe plus.", "ACCOUNT_DELETED");
        }

        userRepository.updateLastLogin(user.getId());

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getPhone(), user.getRoles(), "PENDING");
        String refreshToken = tokenService.createRefreshToken(user.getId());

        log.info("Connexion réussie: userId={}", user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .phone(PhoneUtils.mask(normalizedPhone))
                .roles(user.getRoles())
                .kycStatus("PENDING")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpiryMs() / 1000)
                .otpVerified(user.getStatus() == UserStatus.ACTIVE)
                .build();
    }

    @Override
    @Transactional
    public void logout(UUID userId, String refreshToken) {
        tokenService.revokeRefreshToken(refreshToken);
        log.info("Déconnexion: userId={}", userId);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String rawRefreshToken) {
        var token = tokenService.validateRefreshToken(rawRefreshToken);
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        tokenService.revokeRefreshToken(rawRefreshToken);

        String newAccessToken  = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getPhone(), user.getRoles(), "PENDING");
        String newRefreshToken = tokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .phone(PhoneUtils.mask(user.getPhone()))
                .roles(user.getRoles())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpiryMs() / 1000)
                .build();
    }
}
