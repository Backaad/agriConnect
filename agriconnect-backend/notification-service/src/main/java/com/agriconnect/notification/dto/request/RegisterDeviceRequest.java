package com.agriconnect.notification.dto.request;

import com.agriconnect.notification.domain.enums.Platform;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDeviceRequest {

    @NotBlank(message = "Le FCM token est obligatoire")
    private String fcmToken;

    private Platform platform = Platform.ANDROID;

    private String deviceName;
}
