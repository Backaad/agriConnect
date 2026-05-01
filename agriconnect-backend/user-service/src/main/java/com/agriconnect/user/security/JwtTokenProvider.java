package com.agriconnect.user.security;

import com.agriconnect.commons.security.JwtClaims;
import com.agriconnect.commons.security.SecurityConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.warn("Token invalide: {}", e.getMessage());
            return false;
        }
    }

    public JwtClaims parseToken(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();
        UUID userId = UUID.fromString(claims.getSubject());
        String phone = claims.get(SecurityConstants.CLAIM_PHONE, String.class);
        List<String> roleList = claims.get(SecurityConstants.CLAIM_ROLES, List.class);
        Set<String> roles = roleList != null ? new HashSet<>(roleList) : new HashSet<>();
        String kycStatus = claims.get(SecurityConstants.CLAIM_KYC, String.class);
        return new JwtClaims(userId, phone, roles, kycStatus);
    }
}
