package partner42.modulecommon.exception;

public class SlackException extends InfraException {

    public SlackException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public SlackException(ErrorCode errorCode) {
        super(errorCode);
    }
}
