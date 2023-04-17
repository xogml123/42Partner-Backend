package partner42.modulecommon.exception;

public class SseException extends InfraException{

    public SseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public SseException(ErrorCode errorCode) {
        super(errorCode);
    }
}
