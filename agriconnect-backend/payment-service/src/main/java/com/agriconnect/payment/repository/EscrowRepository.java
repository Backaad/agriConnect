package com.agriconnect.payment.repository;

import com.agriconnect.payment.domain.entity.Escrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EscrowRepository extends JpaRepository<Escrow, Long> {
    Optional<Escrow> findByTaraTransactionId(String taraTransactionId);
}
