package com.seoul.openproject.partner.exception;

import javax.persistence.OptimisticLockException;
import org.springframework.dao.DataAccessException;

public class NotAuthorException extends IllegalArgumentException {

    public NotAuthorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthorException(Throwable cause) {
        super(cause);
    }

    public NotAuthorException(String s) {
    }
}
