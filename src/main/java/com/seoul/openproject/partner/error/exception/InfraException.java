package com.seoul.openproject.partner.error.exception;

public class InfraException extends RuntimeException{

    private final ErrorCode errorCode;

    public InfraException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InfraException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
