package io.identitycontrolplane.auth.exception;

/**
 * Thrown when a refresh token has been revoked due to reuse detection.
 * The entire token family is invalidated and the user must re-authenticate.
 * Maps to 401 UNAUTHORIZED.
 */
public class TokenReuseDetectedException extends AuthException {

    public TokenReuseDetectedException() {
        super("Refresh token reuse detected — all sessions have been revoked. Please log in again.");
    }
}
