package shop.ink3.api.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common errors
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "잘못된 타입의 값입니다."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근이 거부되었습니다."),
    
    // PointPolicy errors
    POINT_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "포인트 정책을 찾을 수 없습니다."),
    POINT_POLICY_ALREADY_EXISTS(HttpStatus.CONFLICT, "P002", "이미 존재하는 포인트 정책입니다."),
    NO_ACTIVE_POINT_POLICY(HttpStatus.NOT_FOUND, "P003", "활성화된 포인트 정책이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
