package com.example.salon_booking.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.salon_booking.exception.ResourceNotFoundException;
import com.example.salon_booking.models.Salon;
import com.example.salon_booking.repositories.SalonRepository;

/**
 * REST Controller for Bridal Salon operations
 * Handles HTTP requests for bridal salon-specific endpoints
 */
@RestController
@RequestMapping("/api/bridal-salons")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class BridalSalonController {

    @Autowired
    private SalonRepository salonRepository;

    /**
     * Get all bridal salons
     * GET /api/bridal-salons
     * 
     * @return List of all bridal salons
     */
    @GetMapping
    public ResponseEntity<List<Salon>> getAllBridalSalons() {
        List<Salon> salons = salonRepository.findByType("bridal-salon");
        return ResponseEntity.ok(salons);
    }

    /**
     * Get a specific bridal salon by ID
     * GET /api/bridal-salons/{id}
     * 
     * @param id The salon ID
     * @return Single salon object
     */
    @GetMapping("/{id}")
    public ResponseEntity<Salon> getBridalSalonById(@PathVariable String id) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bridal salon not found with id: " + id));

        // Verify it's actually a bridal salon
        if (salon.getType() != null && !salon.getType().equals("bridal-salon")) {
            throw new ResourceNotFoundException("Salon with id " + id + " is not a bridal salon");
        }

        return ResponseEntity.ok(salon);
    }
}
