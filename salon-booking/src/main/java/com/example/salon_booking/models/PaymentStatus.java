package com.example.salon_booking.models;

/**
 * Enum representing the payment status of an appointment
 * Tracks whether payment has been made, is pending, or has been refunded
 * 
 * @author Salon Booking System
 * @version 1.0
 */
public enum PaymentStatus {
    /**
     * Payment is pending (pay on arrival or awaiting online payment)
     */
    PENDING("Pending Payment"),
    
    /**
     * Payment has been received and confirmed
     */
    PAID("Paid"),
    
    /**
     * Payment has been refunded (for cancelled appointments)
     */
    REFUNDED("Refunded");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get human-readable display name for the payment status
     * @return Display name string
     */
    public String getDisplayName() {
        return displayName;
    }
}
