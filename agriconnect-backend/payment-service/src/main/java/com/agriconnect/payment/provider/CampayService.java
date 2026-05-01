package com.agriconnect.payment.provider;

import com.agriconnect.payment.domain.enums.PaymentProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CampayService implements PaymentProviderStrategy {

    @Value("${campay.base-url}")
    private String baseUrl;

    @Value("${campay.app-username}")
    private String appUsername;

    @Value("${campay.app-password}")
    private String appPassword;

    @Value("${campay.callback-url}")
    private String callbackUrl;

    @Value("${campay.enabled:false}")
    private boolean enabled;

    private final RestTemplate restTemplate = new RestTemplate();
    private String cachedToken;

    @Override
    public PaymentProvider getProvider() { return PaymentProvider.CAMPAY; }

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public String initiateCollect(String mobileNumber, long amountFcfa, String internalRef, String cbUrl) {
        if (!enabled) {
            log.warn("[DEV] Campay collect simulé: {} FCFA", amountFcfa);
            return "CAMPAY_SIM_" + UUID.randomUUID();
        }

        String token = getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new HashMap<>();
        body.put("amount", String.valueOf(amountFcfa));
        body.put("currency", "XAF");
        body.put("from", normalizePhone(mobileNumber));
        body.put("description", "AgriConnect - " + internalRef);
        body.put("external_reference", internalRef);
        body.put("redirect_url", cbUrl != null ? cbUrl : callbackUrl);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/collect/", request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String ref = (String) response.getBody().get("reference");
                log.info("Campay collect initié: reference={}", ref);
                return ref;
            }
        } catch (Exception e) {
            log.error("Campay collect error: {}", e.getMessage(), e);
        }
        throw new RuntimeException("Échec de l'initialisation Campay");
    }

    @Override
    public String initiateDisbursement(String mobileNumber, long amountFcfa, String internalRef) {
        if (!enabled) {
            log.warn("[DEV] Campay disbursement simulé: {} FCFA", amountFcfa);
            return "CAMPAY_DISB_" + UUID.randomUUID();
        }

        String token = getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new HashMap<>();
        body.put("amount", String.valueOf(amountFcfa));
        body.put("currency", "XAF");
        body.put("to", normalizePhone(mobileNumber));
        body.put("description", "Paiement AgriConnect - " + internalRef);
        body.put("external_reference", internalRef);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/withdraw/", request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("reference");
            }
        } catch (Exception e) {
            log.error("Campay disbursement error: {}", e.getMessage(), e);
        }
        throw new RuntimeException("Échec du disbursement Campay");
    }

    @Override
    public String checkStatus(String providerRef) {
        if (!enabled) return "SUCCESSFUL";
        try {
            String token = getToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/api/transaction/" + providerRef + "/",
                HttpMethod.GET, request, Map.class);
            if (response.getBody() != null) {
                return (String) response.getBody().get("status");
            }
        } catch (Exception e) {
            log.error("Campay status error: {}", e.getMessage());
        }
        return "FAILED";
    }

    private String getToken() {
        if (cachedToken != null) return cachedToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
            "username", appUsername,
            "password", appPassword
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/api/token/", new HttpEntity<>(body, headers), Map.class);

        if (response.getBody() != null) {
            cachedToken = (String) response.getBody().get("token");
            return cachedToken;
        }
        throw new RuntimeException("Impossible d'obtenir le token Campay");
    }

    private String normalizePhone(String phone) {
        return phone.replace("+", "").replace(" ", "");
    }
}
