package com.example.salon_booking.exception;

/**
 * Exception thrown when a requested time slot is not available for booking
 * 
 * @author Salon Booking System
 * @version 1.0
 */
public class TimeSlotNotAvailableException extends RuntimeException {
    
    public TimeSlotNotAvailableException(String message) {
        super(message);
    }
    
    public TimeSlotNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
