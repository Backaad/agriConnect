package com.agriconnect.payment.provider;

import com.agriconnect.payment.domain.enums.PaymentProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OrangeMoneyService implements PaymentProviderStrategy {

    @Value("${orange-money.enabled:false}")
    private boolean enabled;

    @Override
    public PaymentProvider getProvider() { return PaymentProvider.ORANGE_MONEY; }

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public String initiateCollect(String mobileNumber, long amountFcfa, String internalRef, String cbUrl) {
        if (!enabled) {
            log.warn("[DEV] Orange Money collect simulé: {} FCFA vers {}", amountFcfa, mobileNumber);
            return "OM_SIM_" + UUID.randomUUID();
        }
        // TODO: Implémenter l'intégration Orange Money API
        // Ref: https://developer.orange.com/apis/om-webpay-cm
        throw new UnsupportedOperationException("Orange Money prod non configuré");
    }

    @Override
    public String initiateDisbursement(String mobileNumber, long amountFcfa, String internalRef) {
        if (!enabled) {
            log.warn("[DEV] Orange Money disbursement simulé: {} FCFA vers {}", amountFcfa, mobileNumber);
            return "OM_DISB_" + UUID.randomUUID();
        }
        throw new UnsupportedOperationException("Orange Money disbursement non configuré");
    }

    @Override
    public String checkStatus(String providerRef) {
        if (!enabled) return "SUCCESSFUL";
        return "PENDING";
    }
}
