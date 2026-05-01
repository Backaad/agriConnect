package com.agriconnect.payment.domain.entity;

import com.agriconnect.payment.domain.enums.PaymentProvider;
import com.agriconnect.payment.domain.enums.TransactionStatus;
import com.agriconnect.payment.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID walletId;

    @Column(nullable = false, unique = true, length = 100)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TransactionType type;

    @Column(nullable = false)
    private Long amountFcfa;

    @Column(nullable = false)
    @Builder.Default
    private Long feeFcfa = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentProvider provider;

    @Column(length = 200)
    private String providerRef;

    @Column(length = 100)
    private String providerStatus;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(length = 200)
    private String idempotencyKey;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public boolean isPending()  { return status == TransactionStatus.PENDING; }
    public boolean isSuccess()  { return status == TransactionStatus.SUCCESS; }
    public boolean isFailed()   { return status == TransactionStatus.FAILED; }
}
