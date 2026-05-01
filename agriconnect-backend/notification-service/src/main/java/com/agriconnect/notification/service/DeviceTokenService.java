package com.agriconnect.notification.service;

import com.agriconnect.notification.dto.request.RegisterDeviceRequest;
import java.util.UUID;

public interface DeviceTokenService {
    void registerToken(UUID userId, RegisterDeviceRequest request);
    void unregisterToken(UUID userId, String fcmToken);
    void unregisterAllUserDevices(UUID userId);
}
