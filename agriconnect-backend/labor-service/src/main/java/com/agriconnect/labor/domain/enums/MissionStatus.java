package com.agriconnect.labor.domain.enums;
public enum MissionStatus {
    SCHEDULED,   // Planifiée, pas encore démarrée
    IN_PROGRESS, // En cours
    COMPLETED,   // Terminée, en attente de double validation
    VALIDATED,   // Validée par les deux parties → paiement libéré
    DISPUTED,    // En litige
    CANCELLED    // Annulée
}
