package com.seoul.openproject.partner.error.exception;

public class SlackException extends InfraException {

    public SlackException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public SlackException(ErrorCode errorCode) {
        super(errorCode);
    }
}
