package com.example.salon_booking.exception;

/**
 * Exception thrown when attempting to edit a review outside the allowed time window
 * (e.g., after 24 hours)
 */
public class ReviewEditTimeExpiredException extends RuntimeException {
    
    public ReviewEditTimeExpiredException(String message) {
        super(message);
    }
    
    public ReviewEditTimeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
