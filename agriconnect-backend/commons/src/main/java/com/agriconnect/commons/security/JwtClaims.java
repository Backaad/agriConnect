package com.agriconnect.commons.security;

import java.util.Set;
import java.util.UUID;

public record JwtClaims(
        UUID userId,
        String phone,
        Set<String> roles,
        String kycStatus
) {}
