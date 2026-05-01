package com.agriconnect.payment.service;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.payment.dto.request.TopUpRequest;
import com.agriconnect.payment.dto.response.PaymentInitResponse;
import com.agriconnect.payment.dto.response.TransactionResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {
    PaymentInitResponse topUp(UUID userId, TopUpRequest request);
    TransactionResponse getByReference(String reference, UUID userId);
    PageResponse<TransactionResponse> getHistory(UUID userId, String type, Pageable pageable);
    void handleProviderCallback(String provider, String providerRef, String status, String rawPayload);
    void expireStaleTransactions();
}
