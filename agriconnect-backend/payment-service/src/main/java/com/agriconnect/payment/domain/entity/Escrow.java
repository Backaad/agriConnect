package com.agriconnect.payment.domain.entity;

import com.agriconnect.payment.domain.enums.EscrowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "escrows")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Escrow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID referenceId;

    @Column(nullable = false, length = 30)
    private String referenceType;

    @Column(nullable = false)
    private UUID payerId;

    @Column(nullable = false)
    private UUID payeeId;

    @Column(nullable = false)
    private Long amountFcfa;

    @Column(nullable = false)
    @Builder.Default
    private Long platformFee = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EscrowStatus status = EscrowStatus.LOCKED;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime lockedAt = LocalDateTime.now();

    @Column
    private LocalDateTime releasedAt;

    @Column
    private LocalDateTime refundedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(columnDefinition = "TEXT")
    private String releaseReason;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public boolean isLocked()   { return status == EscrowStatus.LOCKED; }
    public boolean isExpired()  { return LocalDateTime.now().isAfter(expiresAt); }

    public long getNetPayeeAmount() {
        return amountFcfa - platformFee;
    }
}
