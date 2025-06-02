package shop.ink3.api.common.exception;

public class MissingHttpRequestContextException extends RuntimeException {
    public MissingHttpRequestContextException() {
        super("No HttpServletRequest available in current context. This method requires an active HTTP request.");
    }
}
