package com.seoul.openproject.partner.exception;

public class NotAuthorException extends RuntimeException {

    public NotAuthorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthorException(Throwable cause) {
        super(cause);
    }

    public NotAuthorException(String s) {
    }
}
