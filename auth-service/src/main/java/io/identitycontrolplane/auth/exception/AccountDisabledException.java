package io.identitycontrolplane.auth.exception;

/**
 * Thrown when a user account is disabled/locked.
 * Maps to 403 FORBIDDEN.
 */
public class AccountDisabledException extends AuthException {

    public AccountDisabledException() {
        super("Account is disabled");
    }
}
