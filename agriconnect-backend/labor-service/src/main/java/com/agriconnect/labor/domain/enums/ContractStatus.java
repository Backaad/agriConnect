package com.agriconnect.labor.domain.enums;
public enum ContractStatus {
    DRAFT,     // Généré, non signé
    SIGNED,    // Signé par les deux parties
    ACTIVE,    // Mission en cours
    COMPLETED, // Mission terminée et validée
    CANCELLED, // Annulé avant démarrage
    DISPUTED   // En litige
}
