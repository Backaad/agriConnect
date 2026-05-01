package com.agriconnect.labor.dto.response;

import com.agriconnect.labor.domain.enums.ContractStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractResponse {
    private UUID id;
    private UUID jobId;
    private UUID farmerId;
    private String farmerName;
    private UUID workerId;
    private String workerName;
    private String workType;
    private LocalDate startDate;
    private Integer durationDays;
    private Long amountFcfa;
    private String locationText;
    private ContractStatus status;
    private LocalDateTime farmerSignedAt;
    private LocalDateTime workerSignedAt;
    private boolean fullySignedByBoth;
    private String escrowRef;
    private LocalDateTime createdAt;
}
