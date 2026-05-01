package com.agriconnect.user.security;

import com.agriconnect.commons.security.JwtClaims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtClaims claims) {
            return claims.userId();
        }
        throw new IllegalStateException("Aucun utilisateur authentifié");
    }
}
