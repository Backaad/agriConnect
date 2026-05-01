package com.agriconnect.commons.security;

public final class SecurityConstants {

    private SecurityConstants() {}

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";

    public static final String CLAIM_USER_ID   = "userId";
    public static final String CLAIM_PHONE     = "phone";
    public static final String CLAIM_ROLES     = "roles";
    public static final String CLAIM_KYC       = "kycStatus";

    public static final String ROLE_FARMER   = "FARMER";
    public static final String ROLE_WORKER   = "WORKER";
    public static final String ROLE_CONSUMER = "CONSUMER";
    public static final String ROLE_ADMIN    = "ADMIN";
}
