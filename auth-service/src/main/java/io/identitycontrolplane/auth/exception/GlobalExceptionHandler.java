package io.identitycontrolplane.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Domain exceptions ──────────────────────────────────────────────────

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ProblemDetail handleAccountDisabled(AccountDisabledException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler({TokenExpiredException.class, TokenRevokedException.class})
    public ProblemDetail handleTokenInvalid(AuthException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(TokenReuseDetectedException.class)
    public ProblemDetail handleTokenReuse(TokenReuseDetectedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setDetail(ex.getMessage());
        return problem;
    }

    // ── Validation ─────────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail(detail);
        return problem;
    }

    // ── Catch-all (never expose internal details) ───────────────────────────

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setDetail("An unexpected error occurred");
        return problem;
    }
}
