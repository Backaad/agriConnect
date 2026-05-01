package com.agriconnect.user.security;

import com.agriconnect.commons.security.JwtClaims;
import com.agriconnect.commons.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
            if (StringUtils.hasText(header) && header.startsWith(SecurityConstants.BEARER_PREFIX)) {
                String token = header.substring(SecurityConstants.BEARER_PREFIX.length());
                if (jwtTokenProvider.validateToken(token)) {
                    JwtClaims claims = jwtTokenProvider.parseToken(token);
                    var authorities = claims.roles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .collect(Collectors.toList());
                    var auth = new UsernamePasswordAuthenticationToken(claims, token, authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            log.error("Erreur JWT: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
