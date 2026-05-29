package io.identitycontrolplane.auth.exception;

/**
 * Abstract base for all domain-level auth exceptions.
 * Extend this — never throw it directly.
 */
public abstract class AuthException extends RuntimeException {

    protected AuthException(String message) {
        super(message);
    }

    protected AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
