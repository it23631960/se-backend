package com.example.salon_booking.exception;

/**
 * Exception thrown when a user attempts to create a duplicate review
 * (i.e., reviewing the same salon twice)
 */
public class DuplicateReviewException extends RuntimeException {
    
    public DuplicateReviewException(String message) {
        super(message);
    }
    
    public DuplicateReviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
