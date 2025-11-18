package com.example.salon_booking.dto;

import com.example.salon_booking.models.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result class for appointment status statistics
 * Contains appointment status and count of appointments with that status
 * Used in owner dashboard overview
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusCountResult {
    
    /**
     * Appointment status
     */
    private AppointmentStatus status;
    
    /**
     * Number of appointments with this status
     */
    private Long count;
    
    /**
     * Get display name for status
     * @return Human-readable status name
     */
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "Unknown";
    }
}
