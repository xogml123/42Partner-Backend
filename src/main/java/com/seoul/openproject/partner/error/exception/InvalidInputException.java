package com.seoul.openproject.partner.error.exception;

public class InvalidInputException extends BusinessException {

    public InvalidInputException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public InvalidInputException(ErrorCode errorCode) {
        super(errorCode);
    }
}

