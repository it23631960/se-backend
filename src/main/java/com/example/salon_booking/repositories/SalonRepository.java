package com.example.salon_booking.repositories;

import com.example.salon_booking.models.Salon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalonRepository extends MongoRepository<Salon, String> {
    List<Salon> findByCategory(String category);
    List<Salon> findByCategoryIgnoreCase(String category);
}