package com.agriconnect.user.service.impl;

import com.agriconnect.user.domain.entity.UserSettings;
import com.agriconnect.user.dto.request.UpdateSettingsRequest;
import com.agriconnect.user.dto.response.UserSettingsResponse;
import com.agriconnect.user.mapper.UserMapper;
import com.agriconnect.user.repository.UserSettingsRepository;
import com.agriconnect.user.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserSettingsRepository settingsRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserSettingsResponse getSettings(UUID userId) {
        UserSettings settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> UserSettings.builder().userId(userId).build());
        return userMapper.toSettingsResponse(settings);
    }

    @Override
    @Transactional
    public UserSettingsResponse updateSettings(UUID userId, UpdateSettingsRequest request) {
        UserSettings settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> UserSettings.builder().userId(userId).build());

        if (request.getLanguage() != null) settings.setLanguage(request.getLanguage());
        if (request.getNotifPush() != null) settings.setNotifPush(request.getNotifPush());
        if (request.getNotifSms() != null) settings.setNotifSms(request.getNotifSms());
        if (request.getNotifEmail() != null) settings.setNotifEmail(request.getNotifEmail());
        if (request.getDarkMode() != null) settings.setDarkMode(request.getDarkMode());
        settings.setUpdatedAt(LocalDateTime.now());

        settingsRepository.save(settings);
        return userMapper.toSettingsResponse(settings);
    }
}
