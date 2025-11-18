package com.example.salon_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result class for service popularity aggregation query
 * Contains service ID, booking count, and total revenue for that service
 * Used in owner dashboard statistics
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePopularityResult {
    
    /**
     * Service ID (MongoDB ObjectId)
     */
    private String serviceId;
    
    /**
     * Total number of bookings for this service
     */
    private Long bookingCount;
    
    /**
     * Total revenue generated from this service
     */
    private Double revenue;
    
    /**
     * Service name (populated after query)
     */
    private String serviceName;
}
