package jp.troter.servlet.httpsession.exception;

public class UserHttpSessionClassNotFoundRuntimeException extends
        RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserHttpSessionClassNotFoundRuntimeException() {
        super();
    }

    public UserHttpSessionClassNotFoundRuntimeException(String message) {
        super(message);
    }

    public UserHttpSessionClassNotFoundRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserHttpSessionClassNotFoundRuntimeException(Throwable cause) {
        super(cause);
    }
}
