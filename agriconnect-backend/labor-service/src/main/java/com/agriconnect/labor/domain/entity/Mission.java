package com.agriconnect.labor.domain.entity;

import com.agriconnect.labor.domain.enums.MissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "missions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(nullable = false)
    private UUID farmerId;

    @Column(nullable = false)
    private UUID workerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private MissionStatus status = MissionStatus.SCHEDULED;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime farmerValidatedAt;

    @Column
    private LocalDateTime workerValidatedAt;

    @Column(columnDefinition = "TEXT")
    private String disputeReason;

    @Column
    private Short farmerRating;

    @Column
    private Short workerRating;

    @Column(columnDefinition = "TEXT")
    private String farmerReview;

    @Column(columnDefinition = "TEXT")
    private String workerReview;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public boolean isFullyValidated() {
        return farmerValidatedAt != null && workerValidatedAt != null;
    }
}
