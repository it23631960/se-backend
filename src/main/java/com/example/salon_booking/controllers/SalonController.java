package com.example.salon_booking.controllers;

import com.example.salon_booking.models.Salon;
import com.example.salon_booking.repositories.SalonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/salons")
public class SalonController {

    @Autowired
    private SalonRepository salonRepository;

    @PostMapping
    public ResponseEntity<Salon> createSalon(@RequestBody Salon salon) {
        return ResponseEntity.ok(salonRepository.save(salon));
    }

    @GetMapping
    public ResponseEntity<List<Salon>> getAllSalons(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(salonRepository.findByCategoryIgnoreCase(category));
        }
        return ResponseEntity.ok(salonRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Salon> getSalonById(@PathVariable String id) {
        Optional<Salon> salon = salonRepository.findById(id);
        return salon.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Salon> updateSalon(@PathVariable String id, @RequestBody Salon salonDetails) {
        Optional<Salon> optionalSalon = salonRepository.findById(id);

        if (optionalSalon.isPresent()) {
            Salon salon = optionalSalon.get();
            salon.setName(salonDetails.getName());
            salon.setDescription(salonDetails.getDescription());
            salon.setCategory(salonDetails.getCategory());
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

            return ResponseEntity.ok(salonRepository.save(salon));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalon(@PathVariable String id) {
        if (salonRepository.existsById(id)) {
            salonRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
