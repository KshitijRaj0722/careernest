package com.careernest.backend.exception;

/** Raised when a user is authenticated but does not own the resource they are acting on. */
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
