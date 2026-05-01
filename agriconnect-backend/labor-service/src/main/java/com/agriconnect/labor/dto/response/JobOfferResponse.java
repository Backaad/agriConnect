package com.agriconnect.labor.dto.response;

import com.agriconnect.labor.domain.enums.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobOfferResponse {
    private UUID id;
    private UUID farmerId;
    private String farmerName;
    private String farmerAvatarUrl;
    private boolean farmerKycVerified;
    private WorkType workType;
    private String workTypeLabel;
    private String description;
    private Integer nbWorkers;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long salaryFcfa;
    private Long totalAmountFcfa;
    private PaymentMethod paymentMethod;
    private Boolean escrowEnabled;
    private Double latitude;
    private Double longitude;
    private String addressText;
    private Integer radiusKm;
    private JobStatus status;
    private List<String> toolsProvided;
    private LocalDateTime expiresAt;
    private Double distanceKm;
    private Integer applicationsCount;
    private LocalDateTime createdAt;
}
