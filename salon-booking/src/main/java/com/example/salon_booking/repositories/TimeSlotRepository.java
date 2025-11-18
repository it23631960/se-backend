package com.example.salon_booking.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.salon_booking.models.TimeSlot;

/**
 * Repository interface for TimeSlot entity
 * Provides database operations for time slot management
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Repository
public interface TimeSlotRepository extends MongoRepository<TimeSlot, String> {
    
    /**
     * Find all available time slots for a specific date
     * @param date Date to search for
     * @return List of available time slots
     */
    List<TimeSlot> findByDateAndIsAvailableTrue(LocalDate date);
    
    /**
     * Find all available time slots for a specific salon and date
     * @param salonId Salon ID
     * @param date Date to search for
     * @return List of available time slots
     */
    List<TimeSlot> findBySalonIdAndDateAndIsAvailableTrue(String salonId, LocalDate date);
    
    /**
     * Find time slot by date and start time
     * @param date Date
     * @param startTime Start time
     * @return Optional containing time slot if found
     */
    Optional<TimeSlot> findByDateAndStartTime(LocalDate date, LocalTime startTime);
    
    /**
     * Find time slots between two dates
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of time slots in range
     */
    List<TimeSlot> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all time slots for a specific salon and date
     * @param salonId Salon ID
     * @param date Date
     * @return List of time slots
     */
    List<TimeSlot> findBySalonIdAndDate(String salonId, LocalDate date);
    
    /**
     * Find available time slots for salon between dates
     * @param salonId Salon ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of available time slots
     */
    List<TimeSlot> findBySalonIdAndDateBetweenAndIsAvailableTrue(
        String salonId, LocalDate startDate, LocalDate endDate);
}
