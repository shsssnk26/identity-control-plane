package io.identitycontrolplane.auth.exception;

/**
 * Thrown when email/password combination is invalid.
 * Maps to 401 UNAUTHORIZED.
 *
 * Intentionally vague message — never tell the client which field was wrong.
 */
public class BadCredentialsException extends AuthException {

    public BadCredentialsException() {
        super("Invalid email or password");
    }
}
