package com.example.salon_booking.models;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TimeSlot entity representing available booking time slots
 * Each slot has a specific date and time range
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Document(collection = "time_slots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "date_time_salon_idx", def = "{'date': 1, 'startTime': 1, 'salon': 1}", unique = true)
public class TimeSlot {
    
    /**
     * Unique identifier for the time slot
     */
    @Id
    private String id;
    
    /**
     * Date of the time slot
     */
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    /**
     * Start time of the slot
     */
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    /**
     * End time of the slot
     */
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    
    /**
     * Whether this slot is available for booking
     */
    @Builder.Default
    private Boolean isAvailable = true;
    
    /**
     * Reference to the salon this slot belongs to
     */
    @DBRef
    @NotNull(message = "Salon is required")
    private Salon salon;
    
    /**
     * Get duration of slot in minutes
     */
    public long getDurationMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }
    
    /**
     * Check if slot is in the past
     */
    public boolean isPast() {
        return date.isBefore(LocalDate.now()) || 
               (date.equals(LocalDate.now()) && startTime.isBefore(LocalTime.now()));
    }
}
