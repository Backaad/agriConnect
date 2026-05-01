package com.agriconnect.payment.service.impl;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.commons.exception.BusinessException;
import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.commons.util.MoneyUtils;
import com.agriconnect.payment.domain.entity.ProviderCallback;
import com.agriconnect.payment.domain.entity.Transaction;
import com.agriconnect.payment.domain.entity.Wallet;
import com.agriconnect.payment.domain.enums.TransactionStatus;
import com.agriconnect.payment.domain.enums.TransactionType;
import com.agriconnect.payment.dto.request.TopUpRequest;
import com.agriconnect.payment.dto.response.PaymentInitResponse;
import com.agriconnect.payment.dto.response.TransactionResponse;
import com.agriconnect.payment.event.model.PaymentSuccessEvent;
import com.agriconnect.payment.event.model.PaymentFailedEvent;
import com.agriconnect.payment.event.publisher.PaymentEventPublisher;
import com.agriconnect.payment.idempotency.IdempotencyKeyService;
import com.agriconnect.payment.mapper.PaymentMapper;
import com.agriconnect.payment.provider.PaymentProviderFactory;
import com.agriconnect.payment.provider.PaymentProviderStrategy;
import com.agriconnect.payment.repository.ProviderCallbackRepository;
import com.agriconnect.payment.repository.TransactionRepository;
import com.agriconnect.payment.repository.WalletRepository;
import com.agriconnect.payment.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final ProviderCallbackRepository callbackRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProviderFactory providerFactory;
    private final PaymentEventPublisher eventPublisher;
    private final IdempotencyKeyService idempotencyService;

    @Override
    @Transactional
    public PaymentInitResponse topUp(UUID userId, TopUpRequest request) {
        // Vérification idempotence
        String iKey = request.getIdempotencyKey() != null
                ? request.getIdempotencyKey()
                : idempotencyService.generateKey(userId, "topup");

        String existing = idempotencyService.getExistingResult(iKey);
        if (existing != null) {
            log.info("Requête dupliquée détectée, retour de la transaction existante: {}", existing);
            Transaction tx = transactionRepository.findByReference(existing)
                    .orElseThrow(() -> new NotFoundException("Transaction", existing));
            return buildPaymentInitResponse(tx);
        }

        if (!idempotencyService.tryAcquire(iKey)) {
            throw new BusinessException("Une transaction identique est déjà en cours", "DUPLICATE_REQUEST");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet introuvable"));

        String reference = "TOPUP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        Transaction transaction = Transaction.builder()
                .walletId(wallet.getId())
                .reference(reference)
                .type(TransactionType.TOPUP)
                .amountFcfa(request.getAmountFcfa())
                .provider(request.getProvider())
                .status(TransactionStatus.PENDING)
                .description("Rechargement wallet — " + request.getMobileNumber())
                .idempotencyKey(iKey)
                .build();

        transaction = transactionRepository.save(transaction);

        PaymentProviderStrategy strategy = providerFactory.getStrategy(request.getProvider());
        try {
            String providerRef = strategy.initiateCollect(
                    request.getMobileNumber(), request.getAmountFcfa(), reference, null);
            transaction.setProviderRef(providerRef);
            transaction.setStatus(TransactionStatus.PROCESSING);
            transactionRepository.save(transaction);
            log.info("TopUp initié: ref={} providerRef={}", reference, providerRef);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            idempotencyService.release(iKey);
            throw new BusinessException("Échec de l'initialisation du paiement: " + e.getMessage(), "PAYMENT_INIT_FAILED");
        }

        idempotencyService.markCompleted(iKey, reference);
        return buildPaymentInitResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getByReference(String reference, UUID userId) {
        Transaction tx = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new NotFoundException("Transaction", reference));
        Wallet wallet = walletRepository.findById(tx.getWalletId())
                .orElseThrow(() -> new NotFoundException("Wallet introuvable"));
        if (!wallet.getUserId().equals(userId)) {
            throw new com.agriconnect.commons.exception.ForbiddenException("Accès refusé à cette transaction");
        }
        return paymentMapper.toTransactionResponse(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getHistory(UUID userId, String type, Pageable pageable) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet introuvable"));
        Page<Transaction> page = type != null
                ? transactionRepository.findByWalletIdAndType(wallet.getId(),
                    TransactionType.valueOf(type.toUpperCase()), pageable)
                : transactionRepository.findByWalletId(wallet.getId(), pageable);
        return PageResponse.from(page.map(paymentMapper::toTransactionResponse));
    }

    @Override
    @Transactional
    public void handleProviderCallback(String provider, String providerRef,
                                        String status, String rawPayload) {
        // Persister le callback brut
        callbackRepository.save(ProviderCallback.builder()
                .provider(provider)
                .reference(providerRef)
                .rawPayload(rawPayload)
                .build());

        transactionRepository.findByProviderRef(providerRef).ifPresent(tx -> {
            tx.setProviderStatus(status);
            boolean success = "SUCCESSFUL".equalsIgnoreCase(status)
                           || "SUCCESS".equalsIgnoreCase(status)
                           || "SUCCESSFUL".equalsIgnoreCase(status);

            if (success && tx.isPending() || success && tx.getStatus() == TransactionStatus.PROCESSING) {
                applySuccessfulTopUp(tx);
            } else if (!success && !tx.isSuccess()) {
                tx.setStatus(TransactionStatus.FAILED);
                tx.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(tx);
                eventPublisher.publishPaymentFailed(PaymentFailedEvent.builder()
                        .transactionRef(tx.getReference())
                        .walletId(tx.getWalletId())
                        .amountFcfa(tx.getAmountFcfa())
                        .reason("Échec opérateur: " + status)
                        .build());
            }
        });
    }

    @Transactional
    public void applySuccessfulTopUp(Transaction tx) {
        Wallet wallet = walletRepository.findByUserIdForUpdate(
                walletRepository.findById(tx.getWalletId())
                    .map(w -> w.getUserId())
                    .orElseThrow())
                .orElseThrow();

        wallet.credit(tx.getAmountFcfa());
        walletRepository.save(wallet);

        tx.setStatus(TransactionStatus.SUCCESS);
        tx.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(tx);

        eventPublisher.publishPaymentSuccess(PaymentSuccessEvent.builder()
                .transactionRef(tx.getReference())
                .walletId(tx.getWalletId())
                .amountFcfa(tx.getAmountFcfa())
                .type(tx.getType())
                .build());

        log.info("TopUp appliqué: wallet={} +{} FCFA", wallet.getId(), tx.getAmountFcfa());
    }

    @Override
    @Scheduled(fixedDelay = 300000)  // toutes les 5 minutes
    @Transactional
    public void expireStaleTransactions() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        transactionRepository.findStalePendingTransactions(cutoff).forEach(tx -> {
            tx.setStatus(TransactionStatus.EXPIRED);
            tx.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(tx);
            log.warn("Transaction expirée: ref={}", tx.getReference());
        });
    }

    private PaymentInitResponse buildPaymentInitResponse(Transaction tx) {
        return PaymentInitResponse.builder()
                .transactionReference(tx.getReference())
                .status(tx.getStatus())
                .providerRef(tx.getProviderRef())
                .amountFcfa(tx.getAmountFcfa())
                .message(tx.getStatus() == TransactionStatus.PROCESSING
                        ? "Confirmez le paiement sur votre téléphone"
                        : tx.getStatus().name())
                .build();
    }
}
