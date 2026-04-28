package com.agriconnect.labor.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MissionRequest {
    private String title;
    private String description;
    private BigDecimal salary;
    private double latitude;
    private double longitude;
}
