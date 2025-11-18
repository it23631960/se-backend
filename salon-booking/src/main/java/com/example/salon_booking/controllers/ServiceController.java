package com.example.salon_booking.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.salon_booking.models.Service;
import com.example.salon_booking.repositories.ServiceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Service operations
 * Handles service-related HTTP requests
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ServiceController {
    
    private final ServiceRepository serviceRepository;
    
    /**
     * Get all services
     * GET /api/services
     */
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        log.info("Fetching all services");
        List<Service> services = serviceRepository.findByActiveTrue();
        return ResponseEntity.ok(services);
    }
    
    /**
     * Get service by ID
     * GET /api/services/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable String id) {
        log.info("Fetching service: {}", id);
        return serviceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get services by category
     * GET /api/services/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Service>> getServicesByCategory(@PathVariable String category) {
        log.info("Fetching services for category: {}", category);
        List<Service> services = serviceRepository.findByCategoryAndActiveTrue(category);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Search services by name
     * GET /api/services/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<Service>> searchServices(@RequestParam String name) {
        log.info("Searching services with name: {}", name);
        List<Service> services = serviceRepository.findByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(services);
    }
}
