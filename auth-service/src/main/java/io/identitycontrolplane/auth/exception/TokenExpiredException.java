package io.identitycontrolplane.auth.exception;

/**
 * Thrown when a refresh token has passed its expiry time.
 * Maps to 401 UNAUTHORIZED — client must re-authenticate.
 */
public class TokenExpiredException extends AuthException {

    public TokenExpiredException() {
        super("Refresh token has expired");
    }
}
