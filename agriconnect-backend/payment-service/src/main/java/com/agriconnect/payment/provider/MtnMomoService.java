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
public class MtnMomoService implements PaymentProviderStrategy {

    @Value("${mtn-momo.base-url}")
    private String baseUrl;

    @Value("${mtn-momo.api-key}")
    private String apiKey;

    @Value("${mtn-momo.subscription-key}")
    private String subscriptionKey;

    @Value("${mtn-momo.target-environment}")
    private String targetEnvironment;

    @Value("${mtn-momo.callback-url}")
    private String callbackUrl;

    @Value("${mtn-momo.enabled:false}")
    private boolean enabled;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public PaymentProvider getProvider() { return PaymentProvider.MTN_MOMO; }

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public String initiateCollect(String mobileNumber, long amountFcfa, String internalRef, String cbUrl) {
        if (!enabled) {
            log.warn("[DEV] MTN MoMo collect simulé: {} FCFA vers {}", amountFcfa, mobileNumber);
            return "MTN_SIM_" + UUID.randomUUID();
        }

        String externalId = UUID.randomUUID().toString();
        HttpHeaders headers = buildHeaders();
        headers.set("X-Reference-Id", externalId);
        headers.set("X-Callback-Url", cbUrl != null ? cbUrl : callbackUrl);

        Map<String, Object> body = new HashMap<>();
        body.put("amount", String.valueOf(amountFcfa));
        body.put("currency", "XAF");
        body.put("externalId", internalRef);
        body.put("payer", Map.of("partyIdType", "MSISDN", "partyId", normalizePhone(mobileNumber)));
        body.put("payerMessage", "Rechargement AgriConnect");
        body.put("payeeNote", "Topup " + internalRef);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                baseUrl + "/collection/v1_0/requesttopay", request, Void.class);
            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                log.info("MTN MoMo collect initié: externalId={}", externalId);
                return externalId;
            }
        } catch (Exception e) {
            log.error("MTN MoMo collect error: {}", e.getMessage(), e);
        }
        throw new RuntimeException("Échec de l'initialisation MTN MoMo");
    }

    @Override
    public String initiateDisbursement(String mobileNumber, long amountFcfa, String internalRef) {
        if (!enabled) {
            log.warn("[DEV] MTN MoMo disbursement simulé: {} FCFA vers {}", amountFcfa, mobileNumber);
            return "MTN_DISB_" + UUID.randomUUID();
        }

        String externalId = UUID.randomUUID().toString();
        HttpHeaders headers = buildHeaders();
        headers.set("X-Reference-Id", externalId);

        Map<String, Object> body = new HashMap<>();
        body.put("amount", String.valueOf(amountFcfa));
        body.put("currency", "XAF");
        body.put("externalId", internalRef);
        body.put("payee", Map.of("partyIdType", "MSISDN", "partyId", normalizePhone(mobileNumber)));
        body.put("payerMessage", "Paiement AgriConnect");
        body.put("payeeNote", "Payment " + internalRef);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                baseUrl + "/disbursement/v1_0/transfer", request, Void.class);
            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                return externalId;
            }
        } catch (Exception e) {
            log.error("MTN MoMo disbursement error: {}", e.getMessage(), e);
        }
        throw new RuntimeException("Échec du disbursement MTN MoMo");
    }

    @Override
    public String checkStatus(String providerRef) {
        if (!enabled) return "SUCCESSFUL";
        try {
            HttpHeaders headers = buildHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/collection/v1_0/requesttopay/" + providerRef,
                HttpMethod.GET, request, Map.class);
            if (response.getBody() != null) {
                return (String) response.getBody().get("status");
            }
        } catch (Exception e) {
            log.error("MTN status check error: {}", e.getMessage());
        }
        return "PENDING";
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
        headers.set("X-Target-Environment", targetEnvironment);
        return headers;
    }

    private String normalizePhone(String phone) {
        return phone.replace("+", "").replace(" ", "");
    }
}
