package com.agriconnect.commons.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }
    public NotFoundException(String resource, String id) {
        super(resource + " introuvable avec l'identifiant : " + id, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }
}
