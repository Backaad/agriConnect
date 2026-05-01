package com.agriconnect.payment.repository;

import com.agriconnect.payment.domain.entity.Transaction;
import com.agriconnect.payment.domain.enums.TransactionStatus;
import com.agriconnect.payment.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByReference(String reference);

    Optional<Transaction> findByIdempotencyKey(String key);

    Page<Transaction> findByWalletId(UUID walletId, Pageable pageable);

    Page<Transaction> findByWalletIdAndType(UUID walletId, TransactionType type, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.walletId = :wId AND t.status = :status")
    Page<Transaction> findByWalletIdAndStatus(
        @Param("wId") UUID walletId,
        @Param("status") TransactionStatus status,
        Pageable pageable
    );

    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' AND t.createdAt < :cutoff")
    List<Transaction> findStalePendingTransactions(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT t FROM Transaction t WHERE t.providerRef = :ref")
    Optional<Transaction> findByProviderRef(@Param("ref") String providerRef);
}
