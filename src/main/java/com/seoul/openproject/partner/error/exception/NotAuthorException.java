package com.seoul.openproject.partner.error.exception;

public class NotAuthorException extends InvalidInputException {

    public NotAuthorException(String message, ErrorCode errorCode) {
        super(message, errorCode);

    }

    public NotAuthorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
