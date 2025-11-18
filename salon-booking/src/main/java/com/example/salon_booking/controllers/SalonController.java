package com.example.salon_booking.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.salon_booking.exception.ResourceNotFoundException;
import com.example.salon_booking.models.Salon;
import com.example.salon_booking.repositories.SalonRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/salons")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class SalonController {

    @Autowired
    private SalonRepository salonRepository;

    @PostMapping
    public ResponseEntity<Salon> createSalon(@Valid @RequestBody Salon salon) {
        Salon savedSalon = salonRepository.save(salon);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSalon);
    }

    @GetMapping
    public ResponseEntity<List<Salon>> getAllSalons() {
        List<Salon> salons = salonRepository.findAll();
        return ResponseEntity.ok(salons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Salon> getSalonById(@PathVariable String id) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id: " + id));
        return ResponseEntity.ok(salon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Salon> updateSalon(@PathVariable String id, @Valid @RequestBody Salon salonDetails) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id: " + id));

        salon.setName(salonDetails.getName());
        salon.setDescription(salonDetails.getDescription());
        salon.setBannerImage(salonDetails.getBannerImage());
        salon.setImages(salonDetails.getImages());
        salon.setReviews(salonDetails.getReviews());
        salon.setAddress(salonDetails.getAddress());
        salon.setPhone(salonDetails.getPhone());
        salon.setEmail(salonDetails.getEmail());
        salon.setServices(salonDetails.getServices());
        salon.setOpenTime(salonDetails.getOpenTime());
        salon.setCloseTime(salonDetails.getCloseTime());
        salon.setAvailable(salonDetails.isAvailable());
        salon.setManager(salonDetails.getManager());
        salon.setBookings(salonDetails.getBookings());
        salon.setSlotsBooked(salonDetails.getSlotsBooked());

        Salon updatedSalon = salonRepository.save(salon);
        return ResponseEntity.ok(updatedSalon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalon(@PathVariable String id) {
        if (!salonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salon not found with id: " + id);
        }
        salonRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
