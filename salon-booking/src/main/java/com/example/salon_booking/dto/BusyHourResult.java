package com.example.salon_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result class for busy hour aggregation query
 * Contains hour of day and number of bookings during that hour
 * Used in owner dashboard to identify peak business hours
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusyHourResult {
    
    /**
     * Hour of the day (0-23)
     * Example: 9 = 9:00 AM, 14 = 2:00 PM
     */
    private Integer hour;
    
    /**
     * Number of bookings during this hour
     */
    private Long bookingCount;
    
    /**
     * Get formatted time string (12-hour format)
     * @return Formatted hour (e.g., "9:00 AM", "2:00 PM")
     */
    public String getFormattedHour() {
        if (hour == null) return "Unknown";
        
        if (hour == 0) return "12:00 AM";
        if (hour < 12) return hour + ":00 AM";
        if (hour == 12) return "12:00 PM";
        return (hour - 12) + ":00 PM";
    }
}
