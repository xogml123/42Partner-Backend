package com.seoul.openproject.partner.error.exception;

public class UnmodifiableArticleException extends InvalidInputException{

    public UnmodifiableArticleException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UnmodifiableArticleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
