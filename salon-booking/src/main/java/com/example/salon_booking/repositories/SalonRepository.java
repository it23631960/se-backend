package com.example.salon_booking.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.salon_booking.models.Salon;

/**
 * Repository interface for Salon entity
 * Provides database operations for salon management
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Repository
public interface SalonRepository extends MongoRepository<Salon, String> {
    
    /**
     * Find salons by name (case-insensitive partial match)
     * @param name Name to search for
     * @return List of matching salons
     */
    List<Salon> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find salons by address (partial match)
     * @param address Address to search for
     * @return List of matching salons
     */
    List<Salon> findByAddressContainingIgnoreCase(String address);
    
    /**
     * Find available salons
     * @param available Availability status
     * @return List of salons
     */
    List<Salon> findByAvailable(boolean available);
    
    /**
     * Find salons by email
     * @param email Email address
     * @return List of salons
     */
    List<Salon> findByEmail(String email);
}