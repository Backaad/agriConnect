package com.agriconnect.payment.domain.enums;
public enum WithdrawalStatus {
    PENDING,    // En attente de traitement
    PROCESSING, // En cours chez l'opérateur
    COMPLETED,  // Fonds reçus sur le compte Mobile Money
    FAILED,     // Échec du retrait
    CANCELLED   // Annulé avant traitement
}
