package com.example.salon_booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for time slot data transfer
 * Contains time slot information for availability display
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    
    private String id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isAvailable;
    private String salonId;
    private String salonName;
    private Long durationMinutes;
    private Boolean isPast;
}
