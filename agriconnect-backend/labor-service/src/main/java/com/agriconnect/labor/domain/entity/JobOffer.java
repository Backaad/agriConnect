package com.agriconnect.labor.domain.entity;

import com.agriconnect.commons.audit.AuditableEntity;
import com.agriconnect.labor.domain.enums.JobStatus;
import com.agriconnect.labor.domain.enums.PaymentMethod;
import com.agriconnect.labor.domain.enums.WorkType;
import com.agriconnect.labor.domain.vo.Location;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "job_offers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobOffer extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID farmerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private WorkType workType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Integer nbWorkers = 1;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Column(nullable = false)
    private Long salaryFcfa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.ANY;

    @Column(nullable = false)
    @Builder.Default
    private Boolean escrowEnabled = false;

    @Embedded
    private Location location;

    @Column(length = 255)
    private String addressText;

    @Column(nullable = false)
    @Builder.Default
    private Integer radiusKm = 10;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private JobStatus status = JobStatus.OPEN;

    @Column
    private LocalDateTime expiresAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "job_offer_tools",
        joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "tool", length = 100)
    @Builder.Default
    private List<String> toolsProvided = new ArrayList<>();

    public boolean isOpen() {
        return status == JobStatus.OPEN &&
               (expiresAt == null || LocalDateTime.now().isBefore(expiresAt));
    }

    public int getDurationDays() {
        if (endDate == null) return 1;
        return (int)(endDate.toEpochDay() - startDate.toEpochDay()) + 1;
    }

    public long getTotalAmount() {
        return salaryFcfa * nbWorkers * getDurationDays();
    }
}
