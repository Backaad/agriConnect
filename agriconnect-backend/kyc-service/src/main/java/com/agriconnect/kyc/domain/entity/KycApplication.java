package com.agriconnect.kyc.domain.entity;

import com.agriconnect.kyc.domain.enums.KycStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kyc_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String cniS3Url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus status;
}
