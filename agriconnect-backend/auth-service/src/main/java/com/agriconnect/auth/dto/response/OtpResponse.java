package com.agriconnect.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse {
    private boolean sent;
    private String maskedPhone;
    private String channel;
    private int expiresInMinutes;
    private String message;
}
