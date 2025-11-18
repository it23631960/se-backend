package com.example.salon_booking.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Service entity representing salon services
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "services")
public class Service {
    
    @Id
    private String id;
    
    @NotBlank(message = "Service name is required")
    private String name;
    
    private String description;
    
    @Min(value = 0, message = "Price must be positive")
    private double price;
    
    @Min(value = 0, message = "Duration must be positive")
    private int durationMinutes; // Duration of the service
    
    private String category; // Hair, Nails, Spa, Makeup, etc.
    
    @Builder.Default
    private boolean active = true; // Whether service is currently offered
}