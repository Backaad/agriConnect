package com.agriconnect.auth.security;

import com.agriconnect.commons.security.JwtClaims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Optional<JwtClaims> getCurrentUserClaims() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtClaims claims) {
            return Optional.of(claims);
        }
        return Optional.empty();
    }

    public static UUID getCurrentUserId() {
        return getCurrentUserClaims()
                .map(JwtClaims::userId)
                .orElseThrow(() -> new IllegalStateException("Aucun utilisateur authentifié"));
    }
}
