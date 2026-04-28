package com.agriconnect.payment.service.impl;

import com.agriconnect.payment.domain.entity.Escrow;
import com.agriconnect.payment.domain.entity.Transaction;
import com.agriconnect.payment.domain.entity.Wallet;
import com.agriconnect.payment.domain.enums.EscrowStatus;
import com.agriconnect.payment.domain.enums.TransactionStatus;
import com.agriconnect.payment.domain.enums.TransactionType;
import com.agriconnect.payment.repository.EscrowRepository;
import com.agriconnect.payment.repository.TransactionRepository;
import com.agriconnect.payment.repository.WalletRepository;
import com.agriconnect.payment.service.EscrowService;
import com.agriconnect.payment.service.TaraApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class EscrowServiceImpl implements EscrowService {

    private final EscrowRepository escrowRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TaraApiService taraApiService;

    // Commission dynamique entre 3% et 5% selon la taille de la transaction par exemple.
    // Pour simplifier, on prend 4%.
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.04");

    @Override
    @Transactional
    public Escrow initiateEscrow(Long missionId, Long farmerId, Long workerId, BigDecimal amount) {
        // 1. Calcul des montants
        BigDecimal commission = amount.multiply(COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal workerAmount = amount.subtract(commission);

        // 2. Appel à l'API Tara pour initier le paiement de l'agriculteur
        // Normalement on récupère le numéro de l'agriculteur depuis un user-service
        String farmerPhone = "237600000000"; // Simulé
        String taraTxId = taraApiService.initiatePaymentRequest(farmerPhone, amount);

        // 3. Création de l'Escrow en base
        Escrow escrow = Escrow.builder()
                .missionId(missionId)
                .farmerId(farmerId)
                .workerId(workerId)
                .amount(amount)
                .commission(commission)
                .workerAmount(workerAmount)
                .status(EscrowStatus.PENDING_PAYMENT)
                .taraTransactionId(taraTxId)
                .build();

        return escrowRepository.save(escrow);
    }

    @Override
    @Transactional
    public Escrow confirmTaraDeposit(String taraTransactionId) {
        Escrow escrow = escrowRepository.findByTaraTransactionId(taraTransactionId)
                .orElseThrow(() -> new RuntimeException("Escrow not found for Tara TX: " + taraTransactionId));

        if (escrow.getStatus() != EscrowStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Escrow is not in PENDING_PAYMENT status.");
        }

        escrow.setStatus(EscrowStatus.HELD);
        Escrow savedEscrow = escrowRepository.save(escrow);

        // Enregistrer la transaction pour l'historique du farmer
        Transaction tx = Transaction.builder()
                .userId(escrow.getFarmerId())
                .type(TransactionType.ESCROW_HOLD)
                .amount(escrow.getAmount())
                .status(TransactionStatus.SUCCESS)
                .referenceId(escrow.getId().toString())
                .description("Paiement mis en séquestre pour la mission " + escrow.getMissionId())
                .build();
        transactionRepository.save(tx);

        return savedEscrow;
    }

    @Override
    @Transactional
    public Escrow releaseEscrow(Long escrowId) {
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found: " + escrowId));

        if (escrow.getStatus() != EscrowStatus.HELD) {
            throw new IllegalStateException("Escrow is not HELD. Cannot release.");
        }

        // Récupérer le wallet du travailleur pour obtenir son numéro de téléphone
        Wallet workerWallet = walletRepository.findByUserId(escrow.getWorkerId())
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUserId(escrow.getWorkerId());
                    w.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(w);
                });

        // Effectuer le transfert via Tara
        String workerPhone = workerWallet.getMobileMoneyNumber() != null ? workerWallet.getMobileMoneyNumber() : "237611111111"; // Simulé
        boolean success = taraApiService.transferToMobileMoney(workerPhone, escrow.getWorkerAmount());

        if (success) {
            escrow.setStatus(EscrowStatus.RELEASED);

            // Transaction pour le travailleur (réception)
            Transaction txWorker = Transaction.builder()
                    .userId(escrow.getWorkerId())
                    .type(TransactionType.ESCROW_RELEASE)
                    .amount(escrow.getWorkerAmount())
                    .status(TransactionStatus.SUCCESS)
                    .referenceId(escrow.getId().toString())
                    .description("Paiement reçu pour la mission " + escrow.getMissionId())
                    .build();
            transactionRepository.save(txWorker);

            // Transaction pour enregistrer la commission plateforme
            Transaction txCommission = Transaction.builder()
                    .userId(0L) // 0 = Système/Plateforme
                    .type(TransactionType.COMMISSION)
                    .amount(escrow.getCommission())
                    .status(TransactionStatus.SUCCESS)
                    .referenceId(escrow.getId().toString())
                    .description("Commission pour la mission " + escrow.getMissionId())
                    .build();
            transactionRepository.save(txCommission);

        } else {
            escrow.setStatus(EscrowStatus.FAILED);
        }

        return escrowRepository.save(escrow);
    }

    @Override
    @Transactional
    public Escrow refundEscrow(Long escrowId) {
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("Escrow not found: " + escrowId));

        if (escrow.getStatus() != EscrowStatus.HELD) {
            throw new IllegalStateException("Escrow is not HELD. Cannot refund.");
        }

        // Remboursement via Tara
        // taraApiService.refundToMobileMoney(...)

        escrow.setStatus(EscrowStatus.REFUNDED);
        return escrowRepository.save(escrow);
    }
}
