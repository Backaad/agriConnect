package com.agriconnect.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicProfileResponse {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String region;
    private String city;
    private String avatarUrl;
    private List<String> languages;
    private List<String> specialties;
    private Set<String> roles;
    private boolean kycVerified;
    private double averageRating;
    private int totalReviews;
}
