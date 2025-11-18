package com.example.salon_booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for customer data transfer
 * Used for creating and updating customer information
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    
    private String id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(\\+94|0)[0-9]{9}$", message = "Invalid phone number")
    private String phone;
    
    private String preferredContact;
    private String notes;
    private LocalDateTime createdAt;
}
