package com.agriconnect.payment.domain.entity;

import com.agriconnect.payment.domain.enums.PaymentProvider;
import com.agriconnect.payment.domain.enums.WithdrawalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "withdrawal_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WithdrawalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID walletId;

    @Column(nullable = false)
    private Long amountFcfa;

    @Column(nullable = false)
    @Builder.Default
    private Long feeFcfa = 0L;

    @Column(nullable = false)
    private Long netAmountFcfa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentProvider provider;

    @Column(nullable = false, length = 20)
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private WithdrawalStatus status = WithdrawalStatus.PENDING;

    @Column(length = 200)
    private String providerRef;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
