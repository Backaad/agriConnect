package com.agriconnect.payment.domain.enums;
public enum TransactionStatus {
    PENDING,      // En attente de confirmation opérateur
    PROCESSING,   // En cours de traitement
    SUCCESS,      // Confirmé et appliqué au wallet
    FAILED,       // Échec de la transaction
    REVERSED,     // Annulé et remboursé
    EXPIRED       // Timeout sans réponse opérateur
}
