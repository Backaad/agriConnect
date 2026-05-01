package com.agriconnect.auth.service;

import com.agriconnect.auth.dto.request.OtpSendRequest;
import com.agriconnect.auth.dto.request.OtpVerifyRequest;
import com.agriconnect.auth.dto.response.OtpResponse;

public interface OtpService {

    OtpResponse sendOtp(OtpSendRequest request);

    boolean verifyOtp(OtpVerifyRequest request);

    void markPhoneVerified(String phone);
}
