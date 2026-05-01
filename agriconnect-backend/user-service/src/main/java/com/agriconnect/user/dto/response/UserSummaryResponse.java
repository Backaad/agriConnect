package com.agriconnect.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {
    private UUID userId;
    private String fullName;
    private String city;
    private String avatarUrl;
    private boolean kycVerified;
    private double averageRating;
}
