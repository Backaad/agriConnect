package com.agriconnect.labor.dto.response;

import com.agriconnect.labor.domain.enums.MissionStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MissionResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal salary;
    private String employerId;
    private double latitude;
    private double longitude;
    private MissionStatus status;
    private LocalDateTime createdAt;
}
