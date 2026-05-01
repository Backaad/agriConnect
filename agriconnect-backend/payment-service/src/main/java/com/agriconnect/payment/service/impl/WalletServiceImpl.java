package com.agriconnect.payment.service.impl;

import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.payment.domain.entity.Wallet;
import com.agriconnect.payment.dto.response.WalletResponse;
import com.agriconnect.payment.mapper.PaymentMapper;
import com.agriconnect.payment.repository.WalletRepository;
import com.agriconnect.payment.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getWallet(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet introuvable pour userId=" + userId));
        return paymentMapper.toWalletResponse(wallet);
    }

    @Override
    @Transactional
    public WalletResponse getOrCreateWallet(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet w = Wallet.builder().userId(userId).build();
                    w = walletRepository.save(w);
                    log.info("Wallet créé pour userId={}", userId);
                    return w;
                });
        return paymentMapper.toWalletResponse(wallet);
    }

    @Override
    @Transactional
    public void ensureWalletExists(UUID userId) {
        if (!walletRepository.existsByUserId(userId)) {
            walletRepository.save(Wallet.builder().userId(userId).build());
            log.info("Wallet initialisé pour userId={}", userId);
        }
    }
}
