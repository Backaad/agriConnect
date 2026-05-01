package com.agriconnect.payment.domain.enums;
public enum EscrowStatus {
    LOCKED,    // Fonds bloqués, en attente de validation
    RELEASED,  // Fonds libérés vers le bénéficiaire
    REFUNDED,  // Fonds remboursés au payeur (litige)
    EXPIRED    // Timeout — retour automatique au payeur
}
