package com.agriconnect.payment.provider;

import com.agriconnect.payment.domain.enums.PaymentProvider;
import com.agriconnect.payment.dto.request.TopUpRequest;

public interface PaymentProviderStrategy {

    PaymentProvider getProvider();

    /**
     * Initie un débit Mobile Money vers notre compte marchand.
     * @return la référence de la transaction chez l'opérateur
     */
    String initiateCollect(String mobileNumber, long amountFcfa, String internalRef, String callbackUrl);

    /**
     * Initie un paiement vers un numéro Mobile Money (retrait, libération escrow).
     * @return la référence de la transaction chez l'opérateur
     */
    String initiateDisbursement(String mobileNumber, long amountFcfa, String internalRef);

    /**
     * Vérifie le statut d'une transaction chez l'opérateur.
     */
    String checkStatus(String providerRef);

    boolean isEnabled();
}
