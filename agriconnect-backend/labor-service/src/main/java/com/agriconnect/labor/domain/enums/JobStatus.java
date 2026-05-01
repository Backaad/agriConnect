package com.agriconnect.labor.domain.enums;
public enum JobStatus {
    OPEN,        // Publiée, visible
    FILLED,      // Travailleurs trouvés, recrutement fermé
    IN_PROGRESS, // Mission en cours
    COMPLETED,   // Mission terminée
    CLOSED,      // Fermée manuellement par l'agriculteur
    EXPIRED      // Date dépassée sans recrutement
}
