package com.agriconnect.payment.service.impl;

import com.agriconnect.commons.exception.BusinessException;
import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.commons.util.MoneyUtils;
import com.agriconnect.payment.domain.entity.Escrow;
import com.agriconnect.payment.domain.entity.Transaction;
import com.agriconnect.payment.domain.entity.Wallet;
import com.agriconnect.payment.domain.enums.EscrowStatus;
import com.agriconnect.payment.domain.enums.TransactionStatus;
import com.agriconnect.payment.domain.enums.TransactionType;
import com.agriconnect.payment.dto.request.EscrowLockRequest;
import com.agriconnect.payment.dto.request.EscrowReleaseRequest;
import com.agriconnect.payment.dto.response.EscrowResponse;
import com.agriconnect.payment.event.model.EscrowLockedEvent;
import com.agriconnect.payment.event.model.EscrowReleasedEvent;
import com.agriconnect.payment.event.publisher.PaymentEventPublisher;
import com.agriconnect.payment.mapper.PaymentMapper;
import com.agriconnect.payment.repository.EscrowRepository;
import com.agriconnect.payment.repository.TransactionRepository;
import com.agriconnect.payment.repository.WalletRepository;
import com.agriconnect.payment.service.EscrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EscrowServiceImpl implements EscrowService {

    private final EscrowRepository escrowRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher eventPublisher;

    @Value("${payment.escrow-timeout-hours:72}")
    private int escrowTimeoutHours;

    @Value("${payment.platform-fee-percent:3.0}")
    private double platformFeePercent;

    @Override
    @Transactional
    public EscrowResponse lock(EscrowLockRequest request) {
        if (escrowRepository.existsByReferenceIdAndStatus(request.getReferenceId(), EscrowStatus.LOCKED)) {
            throw new BusinessException("Un escrow actif existe déjà pour cette référence", "ESCROW_EXISTS");
        }

        Wallet payerWallet = walletRepository.findByUserIdForUpdate(request.getPayerId())
                .orElseThrow(() -> new NotFoundException("Wallet payeur introuvable"));

        if (!payerWallet.hasEnoughFunds(request.getAmountFcfa())) {
            throw new BusinessException(
                "Solde insuffisant. Disponible: " + MoneyUtils.format(payerWallet.getAvailableBalance())
                + " — Requis: " + MoneyUtils.format(request.getAmountFcfa()),
                "INSUFFICIENT_FUNDS"
            );
        }

        long fee = MoneyUtils.platformFee(request.getAmountFcfa());
        payerWallet.freeze(request.getAmountFcfa());
        walletRepository.save(payerWallet);

        Escrow escrow = Escrow.builder()
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .payerId(request.getPayerId())
                .payeeId(request.getPayeeId())
                .amountFcfa(request.getAmountFcfa())
                .platformFee(fee)
                .status(EscrowStatus.LOCKED)
                .expiresAt(LocalDateTime.now().plusHours(escrowTimeoutHours))
                .build();

        escrow = escrowRepository.save(escrow);

        // Enregistrer la transaction ESCROW_LOCK
        transactionRepository.save(Transaction.builder()
                .walletId(payerWallet.getId())
                .reference("ESC-LOCK-" + escrow.getId().toString().substring(0, 8).toUpperCase())
                .type(TransactionType.ESCROW_LOCK)
                .amountFcfa(request.getAmountFcfa())
                .feeFcfa(fee)
                .status(TransactionStatus.SUCCESS)
                .description("Escrow bloqué pour " + request.getReferenceType() + " " + request.getReferenceId())
                .build());

        eventPublisher.publishEscrowLocked(EscrowLockedEvent.builder()
                .escrowId(escrow.getId())
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .payerId(request.getPayerId())
                .payeeId(request.getPayeeId())
                .amountFcfa(request.getAmountFcfa())
                .build());

        log.info("Escrow verrouillé: id={} refId={} amount={} FCFA",
                escrow.getId(), request.getReferenceId(), request.getAmountFcfa());
        return paymentMapper.toEscrowResponse(escrow);
    }

    @Override
    @Transactional
    public EscrowResponse release(EscrowReleaseRequest request) {
        Escrow escrow = escrowRepository.findByReferenceIdAndStatus(
                request.getReferenceId(), EscrowStatus.LOCKED)
                .orElseThrow(() -> new NotFoundException("Escrow actif introuvable pour referenceId=" + request.getReferenceId()));

        if (escrow.isExpired()) {
            throw new BusinessException("L'escrow a expiré, il sera remboursé automatiquement", "ESCROW_EXPIRED");
        }

        Wallet payerWallet = walletRepository.findByUserIdForUpdate(escrow.getPayerId())
                .orElseThrow(() -> new NotFoundException("Wallet payeur introuvable"));

        Wallet payeeWallet = walletRepository.findByUserIdForUpdate(escrow.getPayeeId())
                .orElseThrow(() -> new NotFoundException("Wallet bénéficiaire introuvable"));

        // Débiter le payeur
        payerWallet.unfreeze(escrow.getAmountFcfa());
        payerWallet.debit(escrow.getAmountFcfa());
        walletRepository.save(payerWallet);

        // Créditer le bénéficiaire (montant - frais plateforme)
        long netAmount = escrow.getNetPayeeAmount();
        payeeWallet.credit(netAmount);
        walletRepository.save(payeeWallet);

        escrow.setStatus(EscrowStatus.RELEASED);
        escrow.setReleasedAt(LocalDateTime.now());
        escrow.setReleaseReason(request.getReason());
        escrow.setUpdatedAt(LocalDateTime.now());
        escrow = escrowRepository.save(escrow);

        // Transactions comptables
        transactionRepository.save(Transaction.builder()
                .walletId(payerWallet.getId())
                .reference("ESC-DEBIT-" + escrow.getId().toString().substring(0, 8).toUpperCase())
                .type(TransactionType.ESCROW_RELEASE)
                .amountFcfa(escrow.getAmountFcfa())
                .feeFcfa(escrow.getPlatformFee())
                .status(TransactionStatus.SUCCESS)
                .description("Paiement mission/commande libéré")
                .build());

        transactionRepository.save(Transaction.builder()
                .walletId(payeeWallet.getId())
                .reference("ESC-CREDIT-" + escrow.getId().toString().substring(0, 8).toUpperCase())
                .type(TransactionType.ESCROW_RELEASE)
                .amountFcfa(netAmount)
                .status(TransactionStatus.SUCCESS)
                .description("Paiement reçu pour mission/commande")
                .build());

        eventPublisher.publishEscrowReleased(EscrowReleasedEvent.builder()
                .escrowId(escrow.getId())
                .referenceId(escrow.getReferenceId())
                .payerId(escrow.getPayerId())
                .payeeId(escrow.getPayeeId())
                .amountFcfa(netAmount)
                .build());

        log.info("Escrow libéré: id={} payeeId={} net={} FCFA",
                escrow.getId(), escrow.getPayeeId(), netAmount);
        return paymentMapper.toEscrowResponse(escrow);
    }

    @Override
    @Transactional
    public EscrowResponse refund(UUID referenceId, String reason) {
        Escrow escrow = escrowRepository.findByReferenceIdAndStatus(referenceId, EscrowStatus.LOCKED)
                .orElseThrow(() -> new NotFoundException("Escrow actif introuvable"));

        Wallet payerWallet = walletRepository.findByUserIdForUpdate(escrow.getPayerId())
                .orElseThrow(() -> new NotFoundException("Wallet payeur introuvable"));

        payerWallet.unfreeze(escrow.getAmountFcfa());
        walletRepository.save(payerWallet);

        escrow.setStatus(EscrowStatus.REFUNDED);
        escrow.setRefundedAt(LocalDateTime.now());
        escrow.setReleaseReason(reason);
        escrow.setUpdatedAt(LocalDateTime.now());
        escrow = escrowRepository.save(escrow);

        transactionRepository.save(Transaction.builder()
                .walletId(payerWallet.getId())
                .reference("ESC-REFUND-" + escrow.getId().toString().substring(0, 8).toUpperCase())
                .type(TransactionType.ESCROW_REFUND)
                .amountFcfa(escrow.getAmountFcfa())
                .status(TransactionStatus.SUCCESS)
                .description("Remboursement escrow: " + reason)
                .build());

        log.info("Escrow remboursé: id={} payerId={} amount={} FCFA",
                escrow.getId(), escrow.getPayerId(), escrow.getAmountFcfa());
        return paymentMapper.toEscrowResponse(escrow);
    }

    @Override
    @Transactional(readOnly = true)
    public EscrowResponse getByReference(UUID referenceId) {
        return escrowRepository.findByReferenceIdAndStatus(referenceId, EscrowStatus.LOCKED)
                .or(() -> escrowRepository.findByReferenceIdAndStatus(referenceId, EscrowStatus.RELEASED))
                .map(paymentMapper::toEscrowResponse)
                .orElseThrow(() -> new NotFoundException("Escrow introuvable pour referenceId=" + referenceId));
    }

    @Override
    @Scheduled(fixedDelay = 3600000) // toutes les heures
    @Transactional
    public void expireOldEscrows() {
        List<Escrow> expired = escrowRepository.findExpiredEscrows(LocalDateTime.now());
        expired.forEach(e -> {
            try {
                refund(e.getReferenceId(), "Expiration automatique après " + escrowTimeoutHours + "h");
                log.info("Escrow expiré et remboursé: id={}", e.getId());
            } catch (Exception ex) {
                log.error("Erreur expiration escrow id={}: {}", e.getId(), ex.getMessage());
            }
        });
    }
}
