package com.agriconnect.payment.service;

import com.agriconnect.payment.dto.request.EscrowLockRequest;
import com.agriconnect.payment.dto.request.EscrowReleaseRequest;
import com.agriconnect.payment.dto.response.EscrowResponse;

import java.util.UUID;

public interface EscrowService {
    EscrowResponse lock(EscrowLockRequest request);
    EscrowResponse release(EscrowReleaseRequest request);
    EscrowResponse refund(UUID referenceId, String reason);
    EscrowResponse getByReference(UUID referenceId);
    void expireOldEscrows();
}
