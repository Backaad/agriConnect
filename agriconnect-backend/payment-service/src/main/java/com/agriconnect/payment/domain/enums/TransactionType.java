package com.agriconnect.payment.domain.enums;
public enum TransactionType {
    TOPUP,            // Rechargement wallet via Mobile Money
    PAYMENT,          // Paiement direct (commande marketplace)
    ESCROW_LOCK,      // Blocage fonds pour contrat/commande
    ESCROW_RELEASE,   // Libération fonds après validation
    ESCROW_REFUND,    // Remboursement escrow (litige résolu)
    WITHDRAWAL,       // Retrait vers Mobile Money
    PLATFORM_FEE,     // Commission plateforme
    TRANSFER_IN,      // Crédit reçu d'un autre utilisateur
    TRANSFER_OUT      // Débit vers un autre utilisateur
}
