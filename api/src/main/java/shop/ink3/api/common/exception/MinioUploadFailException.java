package shop.ink3.api.common.exception;

public class MinioUploadFailException extends RuntimeException {
    public MinioUploadFailException(String message) {
        super(message);
    }
}
