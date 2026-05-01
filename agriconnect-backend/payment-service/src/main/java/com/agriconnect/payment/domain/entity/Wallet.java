package com.agriconnect.payment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    @Builder.Default
    private Long balanceFcfa = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long frozenFcfa = 0L;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "XAF";

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    public long getAvailableBalance() {
        return balanceFcfa - frozenFcfa;
    }

    public boolean hasEnoughFunds(long amount) {
        return getAvailableBalance() >= amount;
    }

    public void credit(long amount) {
        this.balanceFcfa += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void debit(long amount) {
        if (this.balanceFcfa < amount) {
            throw new IllegalStateException("Solde insuffisant");
        }
        this.balanceFcfa -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void freeze(long amount) {
        if (getAvailableBalance() < amount) {
            throw new IllegalStateException("Solde disponible insuffisant pour le blocage");
        }
        this.frozenFcfa += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void unfreeze(long amount) {
        if (this.frozenFcfa < amount) {
            throw new IllegalStateException("Montant gelé insuffisant");
        }
        this.frozenFcfa -= amount;
        this.updatedAt = LocalDateTime.now();
    }
}
