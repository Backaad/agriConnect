package com.agriconnect.payment.domain.enums;

public enum EscrowStatus {
    PENDING_PAYMENT, // En attente du paiement de l'agriculteur (via Tara)
    HELD,            // Fonds reçus et bloqués en séquestre
    RELEASED,        // Mission validée, fonds transférés au travailleur
    REFUNDED,        // Mission annulée, fonds remboursés à l'agriculteur
    FAILED           // Échec lors d'une transaction
}
