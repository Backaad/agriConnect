package com.agriconnect.payment.security;

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

@Slf4j @Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    public JwtTokenProvider(@Value("${jwt.secret}") String s) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(s));
    }
    public boolean validateToken(String token) {
        try { Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token); return true; }
        catch (JwtException e) { log.warn("Token invalide: {}", e.getMessage()); return false; }
    }
    public JwtClaims parseToken(String token) {
        Claims c = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        List<String> rl = c.get(SecurityConstants.CLAIM_ROLES, List.class);
        return new JwtClaims(UUID.fromString(c.getSubject()),
            c.get(SecurityConstants.CLAIM_PHONE, String.class),
            rl != null ? new HashSet<>(rl) : new HashSet<>(),
            c.get(SecurityConstants.CLAIM_KYC, String.class));
    }
}
