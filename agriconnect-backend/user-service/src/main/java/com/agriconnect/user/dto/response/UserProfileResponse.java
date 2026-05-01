package com.agriconnect.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String bio;
    private String region;
    private String city;
    private String avatarUrl;
    private List<String> languages;
    private List<String> specialties;
    private Set<String> roles;
    private String kycStatus;
    private LocalDateTime createdAt;
}
