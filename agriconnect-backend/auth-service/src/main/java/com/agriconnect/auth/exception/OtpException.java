package com.agriconnect.auth.exception;

import com.agriconnect.commons.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OtpException extends BaseException {
    public OtpException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "OTP_ERROR");
    }
    public OtpException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
