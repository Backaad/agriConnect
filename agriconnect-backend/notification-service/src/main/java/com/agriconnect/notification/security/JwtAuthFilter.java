package com.agriconnect.notification.security;

import com.agriconnect.commons.security.JwtClaims; import com.agriconnect.commons.security.SecurityConstants;
import jakarta.servlet.FilterChain; import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest; import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.stream.Collectors;

@Component @RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwt;
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        try {
            String h = req.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
            if (StringUtils.hasText(h) && h.startsWith(SecurityConstants.BEARER_PREFIX)) {
                String token = h.substring(SecurityConstants.BEARER_PREFIX.length());
                if (jwt.validateToken(token)) {
                    JwtClaims claims = jwt.parseToken(token);
                    var auth = new UsernamePasswordAuthenticationToken(claims, token,
                        claims.roles().stream().map(r -> new SimpleGrantedAuthority("ROLE_"+r)).collect(Collectors.toList()));
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception ignored) {}
        chain.doFilter(req, res);
    }
}
