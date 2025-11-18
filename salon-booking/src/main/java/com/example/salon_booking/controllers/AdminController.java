package com.example.salon_booking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.salon_booking.repositories.SalonRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Admin Controller for database management
 * USE WITH CAUTION - Contains destructive operations
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
@Slf4j
public class AdminController {

    @Autowired
    private SalonRepository salonRepository;

    /**
     * DELETE all salons from database
     * WARNING: This will delete ALL salon data!
     * After running this, restart the app to re-initialize with type field
     */
    @DeleteMapping("/salons/clear")
    public ResponseEntity<String> clearAllSalons() {
        log.warn("⚠️ ADMIN: Clearing all salons from database...");
        long count = salonRepository.count();
        salonRepository.deleteAll();
        log.info("✅ ADMIN: Deleted {} salons", count);
        return ResponseEntity
                .ok("Deleted " + count + " salons. Restart the application to re-initialize with type field.");
    }
}
