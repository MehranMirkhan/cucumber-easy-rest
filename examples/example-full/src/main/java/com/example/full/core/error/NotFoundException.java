package com.example.full.core.error;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    protected static final HttpStatus status = HttpStatus.NOT_FOUND;

    public NotFoundException() {
        super(status);
    }

    public NotFoundException(String reason) {
        super(status, reason);
    }
}
