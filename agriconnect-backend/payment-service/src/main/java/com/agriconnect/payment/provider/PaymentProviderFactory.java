package com.agriconnect.payment.provider;

import com.agriconnect.payment.domain.enums.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProviderFactory {

    private final List<PaymentProviderStrategy> strategies;

    public PaymentProviderStrategy getStrategy(PaymentProvider provider) {
        return strategies.stream()
                .filter(s -> s.getProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Provider non supporté: " + provider));
    }

    public PaymentProviderStrategy getFallback() {
        // Retourne Campay comme agrégateur de fallback
        return getStrategy(PaymentProvider.CAMPAY);
    }
}
