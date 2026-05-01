package com.agriconnect.payment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "provider_callbacks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProviderCallback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false, length = 30)
    private String provider;

    @Column(length = 200)
    private String reference;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rawPayload;

    @Builder.Default
    @Column(nullable = false)
    private boolean processed = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime receivedAt = LocalDateTime.now();
}
