package com.agriconnect.auth.dto.request;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String phoneNumber;
    private String otp;
}
