package com.careernest.backend.exception;

/** Raised when a job seeker applies to the same job twice. */
public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException(String message) {
        super(message);
    }
}
