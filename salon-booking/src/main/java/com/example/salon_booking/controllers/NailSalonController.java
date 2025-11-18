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
 * REST Controller for Nail Salon operations
 * Handles HTTP requests for nail salon-specific endpoints
 */
@RestController
@RequestMapping("/api/nail-salons")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class NailSalonController {

    @Autowired
    private SalonRepository salonRepository;

    /**
     * Get all nail salons
     * GET /api/nail-salons
     * 
     * @return List of all nail salons
     */
    @GetMapping
    public ResponseEntity<List<Salon>> getAllNailSalons() {
        List<Salon> salons = salonRepository.findByType("nail-salon");
        return ResponseEntity.ok(salons);
    }

    /**
     * Get a specific nail salon by ID
     * GET /api/nail-salons/{id}
     * 
     * @param id The salon ID
     * @return Single salon object
     */
    @GetMapping("/{id}")
    public ResponseEntity<Salon> getNailSalonById(@PathVariable String id) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nail salon not found with id: " + id));

        // Verify it's actually a nail salon
        if (salon.getType() != null && !salon.getType().equals("nail-salon")) {
            throw new ResourceNotFoundException("Salon with id " + id + " is not a nail salon");
        }

        return ResponseEntity.ok(salon);
    }
}
