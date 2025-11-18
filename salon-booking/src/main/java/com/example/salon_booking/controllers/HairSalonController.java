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
 * REST Controller for Hair Salon operations
 * Handles HTTP requests for hair salon-specific endpoints
 */
@RestController
@RequestMapping("/api/hair-salons")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class HairSalonController {

    @Autowired
    private SalonRepository salonRepository;

    /**
     * Get all hair salons
     * GET /api/hair-salons
     * 
     * @return List of all hair salons
     */
    @GetMapping
    public ResponseEntity<List<Salon>> getAllHairSalons() {
        List<Salon> salons = salonRepository.findByType("hair-salon");
        return ResponseEntity.ok(salons);
    }

    /**
     * Get a specific hair salon by ID
     * GET /api/hair-salons/{id}
     * 
     * @param id The salon ID
     * @return Single salon object
     */
    @GetMapping("/{id}")
    public ResponseEntity<Salon> getHairSalonById(@PathVariable String id) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hair salon not found with id: " + id));

        // Verify it's actually a hair salon
        if (salon.getType() != null && !salon.getType().equals("hair-salon")) {
            throw new ResourceNotFoundException("Salon with id " + id + " is not a hair salon");
        }

        return ResponseEntity.ok(salon);
    }
}
