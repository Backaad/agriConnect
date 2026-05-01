package com.agriconnect.auth.exception;

import com.agriconnect.commons.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AuthException extends BaseException {
    public AuthException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "AUTH_ERROR");
    }
    public AuthException(String message, String errorCode) {
        super(message, HttpStatus.UNAUTHORIZED, errorCode);
    }
}
