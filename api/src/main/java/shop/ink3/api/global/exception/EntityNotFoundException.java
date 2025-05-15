package shop.ink3.api.global.exception;

public class EntityNotFoundException extends BaseException {

    public EntityNotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);
    }

    public EntityNotFoundException(String message) {
        super(ErrorCode.ENTITY_NOT_FOUND, message);
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(ErrorCode.ENTITY_NOT_FOUND, cause);
    }
}
