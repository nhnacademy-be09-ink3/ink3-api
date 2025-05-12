package shop.ink3.api.common.advice;

//import org.hibernate.exception.ConstraintViolationException;
//import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.exception.NotFoundException;
//import shop.ink3.api.user.user.exception.InsufficientPointException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<CommonResponse<Void>> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
    }

//    @ExceptionHandler(value = {IllegalStateException.class, InsufficientPointException.class})
//    public ResponseEntity<CommonResponse<Void>> handleBadRequestException(IllegalStateException e) {
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(CommonResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
//    }
//
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<CommonResponse<Void>> handleDuplicateKey(Exception e) {
//        String message = e.getMessage();
//        if (e.getCause() instanceof ConstraintViolationException cve) {
//            if (cve.getMessage().contains("uk_user_login_id")) {
//                message = "Login ID is already in use.";
//            } else if (cve.getMessage().contains("uk_user_email")) {
//                message = "Email is already in use.";
//            }
//        }
//        return ResponseEntity.badRequest().body(CommonResponse.error(HttpStatus.BAD_REQUEST, message));
//    }
}