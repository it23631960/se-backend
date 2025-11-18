package com.example.salon_booking.exception;

/**
 * Exception thrown when a customer cannot be found
 * 
 * @author Salon Booking System
 * @version 1.0
 */
public class CustomerNotFoundException extends RuntimeException {
    
    public CustomerNotFoundException(String message) {
        super(message);
    }
    
    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
