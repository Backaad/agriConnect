package com.agriconnect.labor.dto.response;

import com.agriconnect.labor.domain.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private Long missionId;
    private String workerId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
