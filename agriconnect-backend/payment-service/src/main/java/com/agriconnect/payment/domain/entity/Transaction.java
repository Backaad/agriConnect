package com.agriconnect.payment.domain.entity;

import com.agriconnect.payment.domain.enums.TransactionStatus;
import com.agriconnect.payment.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // ID du Farmer ou Worker

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // DEPOSIT, WITHDRAWAL, ESCROW_HOLD, ESCROW_RELEASE, COMMISSION

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status; // PENDING, SUCCESS, FAILED

    @Column(name = "reference_id")
    private String referenceId; // Référence externe (Tara, etc.) ou ID Escrow

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
