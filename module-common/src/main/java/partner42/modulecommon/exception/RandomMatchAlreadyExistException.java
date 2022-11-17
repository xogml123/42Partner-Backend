package partner42.modulecommon.exception;

public class RandomMatchAlreadyExistException extends
    BusinessException {

    public RandomMatchAlreadyExistException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public RandomMatchAlreadyExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}
