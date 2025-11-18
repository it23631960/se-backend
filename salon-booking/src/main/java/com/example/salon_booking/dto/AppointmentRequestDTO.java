package com.example.salon_booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating appointment requests
 * Contains all necessary information to book an appointment
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    
    /**
     * ID of the salon where appointment is to be booked
     */
    @NotBlank(message = "Salon ID is required")
    private String salonId;
    
    /**
     * ID of the service to be booked
     */
    @NotBlank(message = "Service ID is required")
    private String serviceId;
    
    /**
     * ID of the time slot to be booked
     */
    @NotBlank(message = "Time slot ID is required")
    private String timeSlotId;
    
    /**
     * Customer's full name
     */
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    /**
     * Customer's email address
     */
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    /**
     * Customer's phone number
     */
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^(\\+94|0)[0-9]{9}$", message = "Invalid phone number. Format: +94XXXXXXXXX or 0XXXXXXXXX")
    private String customerPhone;
    
    /**
     * Additional notes or special requests
     */
    private String notes;
    
    /**
     * Preferred contact method
     */
    private String preferredContact;
}
