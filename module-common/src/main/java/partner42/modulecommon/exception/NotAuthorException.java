package partner42.modulecommon.exception;

public class NotAuthorException extends InvalidInputException {

    public NotAuthorException(String message, ErrorCode errorCode) {
        super(message, errorCode);

    }

    public NotAuthorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
