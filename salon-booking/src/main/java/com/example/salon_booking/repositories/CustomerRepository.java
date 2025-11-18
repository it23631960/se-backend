package com.example.salon_booking.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.salon_booking.models.Customer;

/**
 * Repository interface for Customer entity
 * Provides database operations for customer management
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    
    /**
     * Find customer by email address
     * @param email Customer's email
     * @return Optional containing customer if found
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Check if customer exists with given email
     * @param email Email to check
     * @return true if customer exists
     */
    Boolean existsByEmail(String email);
    
    /**
     * Find customers by name (case-insensitive partial match)
     * @param name Name to search for
     * @return List of matching customers
     */
    List<Customer> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find customer by phone number
     * @param phone Phone number
     * @return Optional containing customer if found
     */
    Optional<Customer> findByPhone(String phone);
}
