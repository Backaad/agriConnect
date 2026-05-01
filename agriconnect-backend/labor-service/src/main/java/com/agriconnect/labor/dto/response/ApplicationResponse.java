package com.agriconnect.labor.dto.response;

import com.agriconnect.labor.domain.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponse {
    private UUID id;
    private UUID jobId;
    private String jobTitle;
    private UUID workerId;
    private String workerName;
    private String workerAvatarUrl;
    private boolean workerKycVerified;
    private Double workerRating;
    private Integer workerMissionsCount;
    private String coverNote;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private Double compatibilityScore;
}
