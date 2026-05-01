package com.agriconnect.labor.dto.response;

import com.agriconnect.labor.domain.enums.MissionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MissionResponse {
    private UUID id;
    private UUID contractId;
    private UUID farmerId;
    private String farmerName;
    private UUID workerId;
    private String workerName;
    private MissionStatus status;
    private LocalDate scheduledDate;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime farmerValidatedAt;
    private LocalDateTime workerValidatedAt;
    private String disputeReason;
    private Short farmerRating;
    private Short workerRating;
    private String farmerReview;
    private String workerReview;
    private LocalDateTime createdAt;
}
