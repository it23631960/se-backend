package com.example.salon_booking.models;

/**
 * Enum representing the status of an appointment
 * Defines the lifecycle states of an appointment from creation to completion
 * 
 * Valid transitions:
 * - PENDING → CONFIRMED (salon confirms)
 * - PENDING → CANCELLED (customer/salon cancels)
 * - CONFIRMED → COMPLETED (after appointment time)
 * - CONFIRMED → CANCELLED (customer/salon cancels)
 * - CONFIRMED → NO_SHOW (customer didn't arrive)
 * - COMPLETED → (final state, cannot change)
 * - CANCELLED → (final state, cannot change)
 * - NO_SHOW → (final state, cannot change)
 * 
 * @author Salon Booking System
 * @version 1.0
 */
public enum AppointmentStatus {
    /**
     * Appointment is pending confirmation by salon
     * Initial state when customer books
     */
    PENDING("Pending Confirmation"),
    
    /**
     * Appointment has been confirmed by salon
     * Customer can expect service at scheduled time
     */
    CONFIRMED("Confirmed"),
    
    /**
     * Appointment has been cancelled
     * Can be cancelled by customer or salon
     */
    CANCELLED("Cancelled"),
    
    /**
     * Appointment has been completed successfully
     * Service was provided (final state)
     */
    COMPLETED("Completed"),
    
    /**
     * Customer did not show up for appointment
     * Marked after appointment time passed without customer arrival
     */
    NO_SHOW("No Show");
    
    private final String displayName;
    
    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get human-readable display name for the status
     * @return Display name string
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if this is a final state (cannot be changed)
     * @return true if status is COMPLETED, CANCELLED, or NO_SHOW
     */
    public boolean isFinalState() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }
    
    /**
     * Check if appointment in this status can be cancelled
     * @return true if status is PENDING or CONFIRMED
     */
    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED;
    }
}
