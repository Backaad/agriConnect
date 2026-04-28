package com.agriconnect.payment.domain.entity;

import com.agriconnect.payment.domain.enums.EscrowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "escrows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mission_id", nullable = false)
    private Long missionId;

    @Column(name = "farmer_id", nullable = false)
    private Long farmerId;

    @Column(name = "worker_id", nullable = false)
    private Long workerId;

    @Column(nullable = false)
    private BigDecimal amount; // Montant total payé par l'agriculteur

    @Column(nullable = false)
    private BigDecimal commission; // Commission prélevée (3 à 5%)

    @Column(name = "worker_amount", nullable = false)
    private BigDecimal workerAmount; // Montant à transférer au travailleur (amount - commission)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscrowStatus status;

    @Column(name = "tara_transaction_id")
    private String taraTransactionId; // ID de transaction renvoyé par Tara API

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
