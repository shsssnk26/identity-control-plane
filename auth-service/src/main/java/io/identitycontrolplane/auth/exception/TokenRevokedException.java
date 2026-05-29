package io.identitycontrolplane.auth.exception;

/**
 * Thrown when a refresh token has been revoked (reuse-detection, logout, or rotation).
 * Maps to 401 UNAUTHORIZED — client must re-authenticate.
 */
public class TokenRevokedException extends AuthException {

    public TokenRevokedException() {
        super("Refresh token has been revoked");
    }
}
