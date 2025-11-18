package com.example.salon_booking.exception;

/**
 * Exception thrown when attempting to book a time slot that is already booked
 * 
 * @author Salon Booking System
 * @version 1.0
 */
public class DoubleBookingException extends RuntimeException {
    
    public DoubleBookingException(String message) {
        super(message);
    }
    
    public DoubleBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
