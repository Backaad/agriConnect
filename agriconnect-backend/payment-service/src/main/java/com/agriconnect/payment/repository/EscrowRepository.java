package com.agriconnect.payment.repository;

import com.agriconnect.payment.domain.entity.Escrow;
import com.agriconnect.payment.domain.enums.EscrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EscrowRepository extends JpaRepository<Escrow, UUID> {

    Optional<Escrow> findByReferenceIdAndStatus(UUID referenceId, EscrowStatus status);

    List<Escrow> findByPayerIdAndStatus(UUID payerId, EscrowStatus status);

    List<Escrow> findByPayeeIdAndStatus(UUID payeeId, EscrowStatus status);

    @Query("SELECT e FROM Escrow e WHERE e.status = 'LOCKED' AND e.expiresAt < :now")
    List<Escrow> findExpiredEscrows(@Param("now") LocalDateTime now);

    boolean existsByReferenceIdAndStatus(UUID referenceId, EscrowStatus status);
}
