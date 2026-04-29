package com.agriconnect.payment.controller;

import com.agriconnect.payment.service.EscrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final EscrowService escrowService;

    @PostMapping("/tara")
    public ResponseEntity<String> handleTaraWebhook(@RequestBody Map<String, Object> payload) {
        // En conditions réelles, on vérifierait la signature du webhook ici.
        
        String status = (String) payload.get("status");
        String transactionId = (String) payload.get("transaction_id");

        if ("SUCCESS".equalsIgnoreCase(status) && transactionId != null) {
            escrowService.confirmTaraDeposit(transactionId);
        }

        // On répond toujours 200 OK pour accuser réception
        return ResponseEntity.ok("Webhook received");
    }
}
