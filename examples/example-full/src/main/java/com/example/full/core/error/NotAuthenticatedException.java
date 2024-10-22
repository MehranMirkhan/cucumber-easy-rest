package com.example.full.core.error;

import org.springframework.http.HttpStatus;

public class NotAuthenticatedException extends BaseException {
    protected static final HttpStatus status = HttpStatus.UNAUTHORIZED;

    public NotAuthenticatedException() {
        super(status);
    }

    public NotAuthenticatedException(String reason) {
        super(status, reason);
    }
}
