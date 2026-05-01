package com.agriconnect.payment.service.impl;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.commons.exception.BusinessException;
import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.commons.util.MoneyUtils;
import com.agriconnect.payment.domain.entity.Transaction;
import com.agriconnect.payment.domain.entity.Wallet;
import com.agriconnect.payment.domain.entity.WithdrawalRequest;
import com.agriconnect.payment.domain.enums.*;
import com.agriconnect.payment.dto.request.WithdrawRequest;
import com.agriconnect.payment.dto.response.WithdrawalResponse;
import com.agriconnect.payment.mapper.PaymentMapper;
import com.agriconnect.payment.provider.PaymentProviderFactory;
import com.agriconnect.payment.repository.TransactionRepository;
import com.agriconnect.payment.repository.WalletRepository;
import com.agriconnect.payment.repository.WithdrawalRepository;
import com.agriconnect.payment.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProviderFactory providerFactory;

    @Value("${payment.withdrawal-fee-percent:1.5}")
    private double withdrawalFeePercent;

    @Override
    @Transactional
    public WithdrawalResponse requestWithdrawal(UUID userId, WithdrawRequest request) {
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new NotFoundException("Wallet introuvable"));

        long fee = MoneyUtils.applyPercentage(request.getAmountFcfa(), withdrawalFeePercent);
        long total = request.getAmountFcfa() + fee;
        long netAmount = request.getAmountFcfa();

        if (!wallet.hasEnoughFunds(total)) {
            throw new BusinessException(
                "Solde insuffisant. Disponible: " + MoneyUtils.format(wallet.getAvailableBalance())
                + " — Requis (montant + frais " + withdrawalFeePercent + "%): " + MoneyUtils.format(total),
                "INSUFFICIENT_FUNDS"
            );
        }

        wallet.debit(total);
        walletRepository.save(wallet);

        WithdrawalRequest withdrawal = WithdrawalRequest.builder()
                .userId(userId)
                .walletId(wallet.getId())
                .amountFcfa(request.getAmountFcfa())
                .feeFcfa(fee)
                .netAmountFcfa(netAmount)
                .provider(request.getProvider())
                .mobileNumber(request.getMobileNumber())
                .status(WithdrawalStatus.PROCESSING)
                .build();

        withdrawal = withdrawalRepository.save(withdrawal);

        try {
            String providerRef = providerFactory.getStrategy(request.getProvider())
                    .initiateDisbursement(request.getMobileNumber(), netAmount, withdrawal.getId().toString());
            withdrawal.setProviderRef(providerRef);
            withdrawal.setStatus(WithdrawalStatus.COMPLETED);
            withdrawalRepository.save(withdrawal);

            transactionRepository.save(Transaction.builder()
                    .walletId(wallet.getId())
                    .reference("WD-" + withdrawal.getId().toString().substring(0, 12).toUpperCase())
                    .type(TransactionType.WITHDRAWAL)
                    .amountFcfa(request.getAmountFcfa())
                    .feeFcfa(fee)
                    .status(TransactionStatus.SUCCESS)
                    .provider(request.getProvider())
                    .providerRef(providerRef)
                    .description("Retrait vers " + request.getMobileNumber())
                    .build());

            log.info("Retrait effectué: userId={} amount={} FCFA provider={}",
                    userId, netAmount, request.getProvider());

        } catch (Exception e) {
            // Rembourser si le disbursement échoue
            wallet.credit(total);
            walletRepository.save(wallet);
            withdrawal.setStatus(WithdrawalStatus.FAILED);
            withdrawal.setFailureReason(e.getMessage());
            withdrawalRepository.save(withdrawal);
            throw new BusinessException("Échec du retrait: " + e.getMessage(), "WITHDRAWAL_FAILED");
        }

        return paymentMapper.toWithdrawalResponse(withdrawal);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WithdrawalResponse> getHistory(UUID userId, Pageable pageable) {
        return PageResponse.from(
                withdrawalRepository.findByUserId(userId, pageable)
                        .map(paymentMapper::toWithdrawalResponse)
        );
    }
}
