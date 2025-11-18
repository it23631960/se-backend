package com.example.salon_booking.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.salon_booking.models.Service;

/**
 * Repository interface for Service entity
 */
@Repository
public interface ServiceRepository extends MongoRepository<Service, String> {
    
    /**
     * Find all active services
     * 
     * @return List of active services
     */
    List<Service> findByActiveTrue();
    
    /**
     * Find services by category
     * 
     * @param category Service category
     * @return List of services in the category
     */
    List<Service> findByCategory(String category);
    
    /**
     * Find active services by category
     * 
     * @param category Service category
     * @return List of active services in the category
     */
    List<Service> findByCategoryAndActiveTrue(String category);
    
    /**
     * Find services within a price range
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of services within the price range
     */
    List<Service> findByPriceBetween(double minPrice, double maxPrice);
    
    /**
     * Find active services within a price range
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of active services within the price range
     */
    List<Service> findByPriceBetweenAndActiveTrue(double minPrice, double maxPrice);
    
    /**
     * Search services by name (case-insensitive)
     * 
     * @param name Service name
     * @return List of matching services
     */
    List<Service> findByNameContainingIgnoreCase(String name);
}
