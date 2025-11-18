package com.example.salon_booking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.salon_booking.models.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for appointment response
 * Contains complete appointment information for client display
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    
    private String id;
    private String confirmationCode;
    
    // Customer details
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    // Service details
    private String serviceId;
    private String serviceName;
    private Double servicePrice;
    private Integer serviceDuration;
    
    // Time slot details
    private String timeSlotId;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    
    // Salon details
    private String salonId;
    private String salonName;
    private String salonAddress;
    private String salonPhone;
    
    // Appointment metadata
    private AppointmentStatus status;
    private LocalDateTime bookingDate;
    private LocalDateTime lastModifiedDate;
    private String notes;
    private String assignedStaff;
    private String cancellationReason;
}
