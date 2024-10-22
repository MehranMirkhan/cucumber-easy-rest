package com.example.full.core.error;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class BaseException extends ResponseStatusException {
    public BaseException(HttpStatusCode status) {
        super(status);
    }

    public BaseException(HttpStatusCode status, Throwable cause) {
        super(status, null, cause);
    }

    public BaseException(HttpStatusCode status, String reason) {
        super(status, reason);
    }

    public BaseException(HttpStatusCode status, String reason, Throwable cause) {
        super(status, reason, cause);
    }
}
