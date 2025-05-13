package shop.ink3.api.common.advice;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.exception.NotFoundException;
import shop.ink3.api.user.common.exception.DormantException;
import shop.ink3.api.user.common.exception.InvalidPasswordException;
import shop.ink3.api.user.common.exception.WithdrawnException;
import shop.ink3.api.user.user.exception.InsufficientPointException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<CommonResponse<Void>> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(value = {
            IllegalStateException.class,
            InsufficientPointException.class,
            InvalidPasswordException.class
    })
    public ResponseEntity<CommonResponse<Void>> handleBadRequestException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponse<Void>> handleDuplicateKey(Exception e) {
        String message = e.getMessage();
        if (e.getCause() instanceof ConstraintViolationException cve) {
            if (cve.getMessage().contains("uk_user_login_id") || cve.getMessage().contains("uk_admin_login_id")) {
                message = "Login ID is already in use.";
            } else if (cve.getMessage().contains("uk_user_email")) {
                message = "Email is already in use.";
            }
        }
        return ResponseEntity.badRequest().body(CommonResponse.error(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(DormantException.class)
    public ResponseEntity<CommonResponse<Void>> handleDormantException(DormantException e) {
        return ResponseEntity.status(HttpStatus.LOCKED).body(CommonResponse.error(HttpStatus.LOCKED, e.getMessage()));
    }

    @ExceptionHandler(WithdrawnException.class)
    public ResponseEntity<CommonResponse<Void>> handleWithdrawnException(WithdrawnException e) {
        return ResponseEntity.status(HttpStatus.GONE).body(CommonResponse.error(HttpStatus.GONE, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
}