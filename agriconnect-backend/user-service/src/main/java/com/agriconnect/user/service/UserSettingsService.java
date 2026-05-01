package com.agriconnect.user.service;

import com.agriconnect.user.dto.request.UpdateSettingsRequest;
import com.agriconnect.user.dto.response.UserSettingsResponse;

import java.util.UUID;

public interface UserSettingsService {

    UserSettingsResponse getSettings(UUID userId);

    UserSettingsResponse updateSettings(UUID userId, UpdateSettingsRequest request);
}
