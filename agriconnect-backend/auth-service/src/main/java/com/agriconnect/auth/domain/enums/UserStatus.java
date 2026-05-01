package com.agriconnect.auth.domain.enums;

public enum UserStatus {
    PENDING,      // Inscrit, OTP non vérifié
    ACTIVE,       // OTP vérifié, compte actif
    SUSPENDED,    // Compte suspendu par admin
    DELETED       // Suppression logique
}
