//package shop.ink3.api.global.exception;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.validation.BindException;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//
//@Slf4j
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    /**
//     * uc0acuc6a9uc790 uc815uc758 uc608uc678 ucc98ub9ac
//     */
//    @ExceptionHandler(BaseException.class)
//    protected ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
//        log.error("handleBaseException", e);
//        ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
//        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
//    }
//
//    /**
//     * uc785ub825 uac12 uc720ud6a8uc131 uac80uc0ac uc608uc678 ucc98ub9ac
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        log.error("handleMethodArgumentNotValidException", e);
//        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    /**
//     * ubc14uc778ub529 uc608uc678 ucc98ub9ac
//     */
//    @ExceptionHandler(BindException.class)
//    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
//        log.error("handleBindException", e);
//        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    /**
//     * enum ud0c0uc785 ubd88uc77cuce58 uc608uc678 ucc98ub9ac
//     */
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
//        log.error("handleMethodArgumentTypeMismatchException", e);
//        ErrorResponse response = ErrorResponse.of(e);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    /**
//     * JSON ud30cuc2f1 uc608uc678 ucc98ub9ac
//     */
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
//        log.error("handleHttpMessageNotReadableException", e);
//        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    /**
//     * HTTP uba54uc11cub4dc uc9c0uc6d0 uc548ud568 uc608uc678 ucc98ub9ac
//     */
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
//        log.error("handleHttpRequestMethodNotSupportedException", e);
//        ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
//        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
//    }
//
//    /**
//     * ub098uba38uc9c0 uc608uc678 ucc98ub9ac
//     */
//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
//        log.error("handleException", e);
//        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}
