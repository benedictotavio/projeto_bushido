package br.org.institutobushido.resources.handlers;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

@RestControllerAdvice
public class AuthenticatedExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception e) {
        ProblemDetail problem = null;

        if (e instanceof BadCredentialsException) {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), e.getMessage());
            problem.setTitle("Authentication Error");
            problem.setProperty("access_denied", "Authentication Error");
        }

        if (e instanceof AccessDeniedException) {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), e.getMessage());
            problem.setTitle("Access Denied Error");
            problem.setProperty("access_denied", "Not authorized to access this resource.");
        }

        if (e instanceof TokenExpiredException) {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), e.getMessage());
            problem.setTitle("JWT Expired");
            problem.setProperty("jwt_error", "JWT Token is expired.");
        }

        if (e instanceof SignatureVerificationException) {
            problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), e.getMessage());
            problem.setTitle("JWT Signature error");
            problem.setProperty("jwt_error", "JWT Signature is invalid.");
        }
        return problem;
    }
}
