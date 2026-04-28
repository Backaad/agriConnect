package com.agriconnect.labor.domain.entity;

import com.agriconnect.labor.domain.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Lob
    @Column(name = "pdf_data")
    private byte[] pdfData;

    @Column(name = "employer_signed")
    private boolean employerSigned;

    @Column(name = "worker_signed")
    private boolean workerSigned;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ContractStatus.PENDING_SIGNATURE;
        }
    }
}
