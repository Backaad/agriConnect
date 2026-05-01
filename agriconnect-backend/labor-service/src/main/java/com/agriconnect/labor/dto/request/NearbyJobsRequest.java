package com.agriconnect.labor.dto.request;

import com.agriconnect.labor.domain.enums.WorkType;
import lombok.Data;

@Data
public class NearbyJobsRequest {
    private Double latitude;
    private Double longitude;
    private Integer radiusKm = 20;
    private WorkType workType;
    private Long minSalary;
}
