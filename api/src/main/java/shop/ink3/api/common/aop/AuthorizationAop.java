package shop.ink3.api.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import shop.ink3.api.common.annotation.Authorization;
import shop.ink3.api.common.exception.AccessDeniedException;
import shop.ink3.api.common.exception.MissingHttpRequestContextException;

@Aspect
@Component
public class AuthorizationAop {
    @Before("@annotation(authorization)")
    public void checkRole(Authorization authorization) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            throw new MissingHttpRequestContextException();
        }
        HttpServletRequest request = attributes.getRequest();
        String role = request.getHeader("X-User-Role");

        if (Objects.isNull(role)) {
            throw new AccessDeniedException("Access denied: missing role header.");
        }

        boolean hasAccess = Arrays.stream(authorization.value()).anyMatch(role::equalsIgnoreCase);

        if (!hasAccess) {
            throw new AccessDeniedException("Access denied: role '%s' is not permitted.".formatted(role));
        }
    }
}
