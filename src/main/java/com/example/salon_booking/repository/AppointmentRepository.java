package com.example.salon_booking.repository;

import com.example.salon_booking.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    // Custom query methods can be added here
}