package com.example.salon_booking.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Customer entity representing a salon customer
 * Stores customer information for appointment booking
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Document(collection = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    /**
     * Unique identifier for the customer
     */
    @Id
    private String id;
    
    /**
     * Customer's full name
     */
    @NotBlank(message = "Name is required")
    private String name;
    
    /**
     * Customer's email address (unique)
     */
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Indexed(unique = true)
    private String email;
    
    /**
     * Customer's phone number
     * Format: +94XXXXXXXXX or 0XXXXXXXXX (Sri Lankan format)
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+94|0)[0-9]{9}$", message = "Invalid phone number. Format: +94XXXXXXXXX or 0XXXXXXXXX")
    private String phone;
    
    /**
     * Timestamp when customer was created
     */
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * Customer's preferred communication method
     */
    private String preferredContact; // EMAIL, PHONE, SMS
    
    /**
     * Additional notes about the customer
     */
    private String notes;
}
