package com.seoul.openproject.partner.exception;

public class SlackBotException extends RuntimeException {

    public SlackBotException() {
        super();
    }

    public SlackBotException(String message) {
        super(message);
    }

    public SlackBotException(String message, Throwable cause) {
        super(message, cause);
    }

    public SlackBotException(Throwable cause) {
        super(cause);
    }
}
