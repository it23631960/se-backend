package com.example.salon_booking.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result class for daily statistics aggregation
 * Contains date, appointment count, and revenue for that day
 * Used for charts and graphs in owner dashboard
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatsResult {
    
    /**
     * Date for these statistics
     */
    private LocalDate date;
    
    /**
     * Number of appointments on this date
     */
    private Long appointmentCount;
    
    /**
     * Total revenue earned on this date
     */
    private Double revenue;
    
    /**
     * Get formatted date string
     * @return Date in YYYY-MM-DD format
     */
    public String getFormattedDate() {
        return date != null ? date.toString() : "Unknown";
    }
    
    /**
     * Get average revenue per appointment
     * @return Average revenue or 0 if no appointments
     */
    public Double getAverageRevenuePerAppointment() {
        if (appointmentCount == null || appointmentCount == 0 || revenue == null) {
            return 0.0;
        }
        return revenue / appointmentCount;
    }
}
