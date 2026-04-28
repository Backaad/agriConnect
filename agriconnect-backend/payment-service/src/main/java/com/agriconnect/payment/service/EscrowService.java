package com.agriconnect.payment.service;

import com.agriconnect.payment.domain.entity.Escrow;

import java.math.BigDecimal;

public interface EscrowService {

    /**
     * Initie un paiement avec mise en séquestre via l'API Tara.
     */
    Escrow initiateEscrow(Long missionId, Long farmerId, Long workerId, BigDecimal amount);

    /**
     * Valide la mission et transfère les fonds du séquestre au travailleur.
     */
    Escrow releaseEscrow(Long escrowId);

    /**
     * Annule la mission et rembourse l'agriculteur.
     */
    Escrow refundEscrow(Long escrowId);

    /**
     * Confirme le dépôt suite au Webhook de Tara.
     */
    Escrow confirmTaraDeposit(String taraTransactionId);
}
