package com.agriconnect.labor.security;

import com.agriconnect.commons.security.JwtClaims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public final class SecurityUtils {
    private SecurityUtils() {}
    public static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtClaims c) return c.userId();
        throw new IllegalStateException("Aucun utilisateur authentifié");
    }
    public static boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
