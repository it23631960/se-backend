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
 * REST Controller for Barber Shop operations
 * Handles HTTP requests for barber shop-specific endpoints
 */
@RestController
@RequestMapping("/api/barber-shops")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class BarberShopController {

    @Autowired
    private SalonRepository salonRepository;

    /**
     * Get all barber shops
     * GET /api/barber-shops
     * 
     * @return List of all barber shops
     */
    @GetMapping
    public ResponseEntity<List<Salon>> getAllBarberShops() {
        List<Salon> salons = salonRepository.findByType("barber-shop");
        return ResponseEntity.ok(salons);
    }

    /**
     * Get a specific barber shop by ID
     * GET /api/barber-shops/{id}
     * 
     * @param id The salon ID
     * @return Single salon object
     */
    @GetMapping("/{id}")
    public ResponseEntity<Salon> getBarberShopById(@PathVariable String id) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber shop not found with id: " + id));

        // Verify it's actually a barber shop
        if (salon.getType() != null && !salon.getType().equals("barber-shop")) {
            throw new ResourceNotFoundException("Salon with id " + id + " is not a barber shop");
        }

        return ResponseEntity.ok(salon);
    }
}
