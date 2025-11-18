package com.example.salon_booking.exception;

/**
 * Exception thrown when a user attempts an unauthorized action on a review
 * (e.g., editing or deleting another user's review)
 */
public class UnauthorizedReviewActionException extends RuntimeException {
    
    public UnauthorizedReviewActionException(String message) {
        super(message);
    }
    
    public UnauthorizedReviewActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
