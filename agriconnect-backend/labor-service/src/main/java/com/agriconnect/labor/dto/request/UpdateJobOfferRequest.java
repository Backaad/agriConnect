package com.agriconnect.labor.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class UpdateJobOfferRequest {

    @Size(min = 20, max = 1000)
    private String description;

    @Min(1) @Max(50)
    private Integer nbWorkers;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    @Min(500) @Max(500000)
    private Long salaryFcfa;

    private Double latitude;
    private Double longitude;
    private String addressText;

    @Min(1) @Max(100)
    private Integer radiusKm;

    private List<String> toolsProvided;
}
