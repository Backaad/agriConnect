package com.agriconnect.labor.domain.entity;

import com.agriconnect.labor.domain.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contracts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobOffer job;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(nullable = false)
    private UUID farmerId;

    @Column(nullable = false)
    private UUID workerId;

    @Column(nullable = false)
    private Long amountFcfa;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false, length = 50)
    private String workType;

    @Column(length = 255)
    private String locationText;

    @Column(nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ContractStatus status = ContractStatus.DRAFT;

    @Column
    private LocalDateTime farmerSignedAt;

    @Column
    private LocalDateTime workerSignedAt;

    @Column(length = 100)
    private String escrowRef;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public boolean isFullySigned() {
        return farmerSignedAt != null && workerSignedAt != null;
    }
}
