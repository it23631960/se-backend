package com.example.salon_booking.exception;

/**
 * Exception thrown when an invalid appointment operation is attempted
 * 
 * @author Salon Booking System
 * @version 1.0
 */
public class InvalidAppointmentException extends RuntimeException {
    
    public InvalidAppointmentException(String message) {
        super(message);
    }
    
    public InvalidAppointmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
