package com.agriconnect.user.mapper;

import com.agriconnect.user.domain.entity.UserProfile;
import com.agriconnect.user.domain.entity.UserSettings;
import com.agriconnect.user.dto.request.UpdateProfileRequest;
import com.agriconnect.user.dto.response.PublicProfileResponse;
import com.agriconnect.user.dto.response.UserProfileResponse;
import com.agriconnect.user.dto.response.UserSettingsResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(profile.getFullName())")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "kycStatus", ignore = true)
    UserProfileResponse toProfileResponse(UserProfile profile);

    @Mapping(target = "kycVerified", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    PublicProfileResponse toPublicResponse(UserProfile profile);

    UserSettingsResponse toSettingsResponse(UserSettings settings);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UpdateProfileRequest request, @MappingTarget UserProfile profile);
}
