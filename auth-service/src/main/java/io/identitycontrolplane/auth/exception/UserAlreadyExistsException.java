package io.identitycontrolplane.auth.exception;

/**
 * Thrown when registration is attempted with an email already in use.
 * Maps to 409 CONFLICT.
 */
public class UserAlreadyExistsException extends AuthException {

    public UserAlreadyExistsException(String email) {
        super("Email already in use: " + email);
    }
}
