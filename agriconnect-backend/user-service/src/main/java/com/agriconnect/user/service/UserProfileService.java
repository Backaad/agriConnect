package com.agriconnect.user.service;

import com.agriconnect.user.dto.request.UpdateProfileRequest;
import com.agriconnect.user.dto.response.PublicProfileResponse;
import com.agriconnect.user.dto.response.UserProfileResponse;
import com.agriconnect.commons.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UserProfileService {

    UserProfileResponse getMyProfile(UUID userId);

    UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request);

    String updateAvatar(UUID userId, MultipartFile file) throws IOException;

    PublicProfileResponse getPublicProfile(UUID userId);

    PageResponse<PublicProfileResponse> searchProfiles(String query, Pageable pageable);
}
