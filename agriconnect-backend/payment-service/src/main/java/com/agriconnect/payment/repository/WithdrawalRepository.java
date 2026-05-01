package com.agriconnect.payment.repository;

import com.agriconnect.payment.domain.entity.WithdrawalRequest;
import com.agriconnect.payment.domain.enums.WithdrawalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalRequest, UUID> {

    Page<WithdrawalRequest> findByUserId(UUID userId, Pageable pageable);

    List<WithdrawalRequest> findByStatus(WithdrawalStatus status);

    Optional<WithdrawalRequest> findByProviderRef(String providerRef);
}
