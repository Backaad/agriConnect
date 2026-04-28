package com.agriconnect.payment.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TaraApiService {

    // Simule un appel à l'API Tara pour initier un paiement
    public String initiatePaymentRequest(String phoneNumber, BigDecimal amount) {
        // En réalité, on ferait un appel HTTP REST vers Dikkalo/Tara.
        // On retourne un ID de transaction simulé.
        return "TARA-" + UUID.randomUUID().toString();
    }

    // Simule un transfert vers un compte Mobile Money via Tara
    public boolean transferToMobileMoney(String phoneNumber, BigDecimal amount) {
        // En réalité, on appellerait l'endpoint de transfert (Payout) de Tara.
        return true;
    }
}
