package com.agriconnect.payment.service;

import com.agriconnect.payment.dto.response.WalletResponse;

import java.util.UUID;

public interface WalletService {
    WalletResponse getOrCreateWallet(UUID userId);
    WalletResponse getWallet(UUID userId);
    void ensureWalletExists(UUID userId);
}
