package com.agriconnect.payment.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.payment.service.TransactionService;
import com.agriconnect.payment.webhook.WebhookSignatureVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final TransactionService transactionService;
    private final WebhookSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper;

    @PostMapping("/mtn")
    public ResponseEntity<ApiResponse<Void>> mtnCallback(
            @RequestBody String payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {
        log.info("MTN MoMo webhook reçu");
        try {
            JsonNode node = objectMapper.readTree(payload);
            String ref    = node.path("externalId").asText();
            String status = node.path("status").asText();
            transactionService.handleProviderCallback("MTN_MOMO", ref, status, payload);
        } catch (Exception e) {
            log.error("Erreur traitement webhook MTN: {}", e.getMessage(), e);
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Reçu"));
    }

    @PostMapping("/orange")
    public ResponseEntity<ApiResponse<Void>> orangeCallback(@RequestBody String payload) {
        log.info("Orange Money webhook reçu");
        try {
            JsonNode node = objectMapper.readTree(payload);
            String ref    = node.path("txnid").asText();
            String status = node.path("status").asText();
            transactionService.handleProviderCallback("ORANGE_MONEY", ref, status, payload);
        } catch (Exception e) {
            log.error("Erreur traitement webhook Orange: {}", e.getMessage(), e);
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Reçu"));
    }

    @PostMapping("/campay")
    public ResponseEntity<ApiResponse<Void>> campayCallback(@RequestBody String payload) {
        log.info("Campay webhook reçu");
        try {
            JsonNode node = objectMapper.readTree(payload);
            String ref    = node.path("reference").asText();
            String status = node.path("status").asText();
            transactionService.handleProviderCallback("CAMPAY", ref, status, payload);
        } catch (Exception e) {
            log.error("Erreur traitement webhook Campay: {}", e.getMessage(), e);
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Reçu"));
    }
}
