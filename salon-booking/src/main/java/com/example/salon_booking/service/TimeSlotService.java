package com.example.salon_booking.service;

import com.example.salon_booking.dto.TimeSlotDTO;
import com.example.salon_booking.exception.ResourceNotFoundException;
import com.example.salon_booking.exception.TimeSlotNotAvailableException;
import com.example.salon_booking.models.Salon;
import com.example.salon_booking.models.TimeSlot;
import com.example.salon_booking.repositories.SalonRepository;
import com.example.salon_booking.repositories.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing time slot operations
 * Handles time slot creation, availability checks, and slot generation
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TimeSlotService {
    
    private final TimeSlotRepository timeSlotRepository;
    private final SalonRepository salonRepository;
    
    // Constants for slot generation
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(18, 0);  // 6:00 PM
    private static final int SLOT_DURATION_MINUTES = 30;
    
    /**
     * Get all available time slots for a salon on a specific date
     * @param salonId Salon ID
     * @param date Date to check
     * @return List of available time slots
     */
    public List<TimeSlot> getAvailableSlots(String salonId, LocalDate date) {
        log.info("Fetching available slots for salon {} on {}", salonId, date);
        
        // Verify salon exists
        if (!salonRepository.existsById(salonId)) {
            throw new ResourceNotFoundException("Salon not found with id: " + salonId);
        }
        
        List<TimeSlot> slots = timeSlotRepository.findBySalonIdAndDateAndIsAvailableTrue(salonId, date);
        
        // Filter out past slots
        if (date.equals(LocalDate.now())) {
            LocalTime now = LocalTime.now();
            slots = slots.stream()
                    .filter(slot -> slot.getStartTime().isAfter(now))
                    .collect(Collectors.toList());
        }
        
        log.info("Found {} available slots", slots.size());
        return slots;
    }
    
    /**
     * Get available slots for a date range
     * @param salonId Salon ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of available time slots
     */
    public List<TimeSlot> getAvailableSlotsInRange(String salonId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching available slots for salon {} from {} to {}", salonId, startDate, endDate);
        return timeSlotRepository.findBySalonIdAndDateBetweenAndIsAvailableTrue(salonId, startDate, endDate);
    }
    
    /**
     * Generate time slots for a salon for a specific week
     * @param salonId Salon ID
     * @param startDate Start date of the week
     */
    @Transactional
    public void generateSlotsForWeek(String salonId, LocalDate startDate) {
        log.info("Generating slots for salon {} starting from {}", salonId, startDate);
        
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id: " + salonId));
        
        List<TimeSlot> slotsToCreate = new ArrayList<>();
        
        // Generate slots for 7 days
        for (int day = 0; day < 7; day++) {
            LocalDate date = startDate.plusDays(day);
            
            // Skip past dates
            if (date.isBefore(LocalDate.now())) {
                continue;
            }
            
            // Check if slots already exist for this date
            List<TimeSlot> existingSlots = timeSlotRepository.findBySalonIdAndDate(salonId, date);
            if (!existingSlots.isEmpty()) {
                log.info("Slots already exist for {} on {}, skipping", salonId, date);
                continue;
            }
            
            // Parse salon open/close times or use defaults
            LocalTime startTime = parseTime(salon.getOpenTime(), DEFAULT_START_TIME);
            LocalTime endTime = parseTime(salon.getCloseTime(), DEFAULT_END_TIME);
            
            // Generate slots for this day
            LocalTime currentTime = startTime;
            while (currentTime.plusMinutes(SLOT_DURATION_MINUTES).isBefore(endTime) || 
                   currentTime.plusMinutes(SLOT_DURATION_MINUTES).equals(endTime)) {
                
                TimeSlot slot = TimeSlot.builder()
                        .date(date)
                        .startTime(currentTime)
                        .endTime(currentTime.plusMinutes(SLOT_DURATION_MINUTES))
                        .isAvailable(true)
                        .salon(salon)
                        .build();
                
                slotsToCreate.add(slot);
                currentTime = currentTime.plusMinutes(SLOT_DURATION_MINUTES);
            }
        }
        
        if (!slotsToCreate.isEmpty()) {
            timeSlotRepository.saveAll(slotsToCreate);
            log.info("Created {} time slots for salon {}", slotsToCreate.size(), salonId);
        }
    }
    
    /**
     * Generate slots for multiple salons
     * @param startDate Start date
     */
    @Transactional
    public void generateSlotsForAllSalons(LocalDate startDate) {
        log.info("Generating slots for all salons starting from {}", startDate);
        
        List<Salon> salons = salonRepository.findAll();
        
        for (Salon salon : salons) {
            try {
                generateSlotsForWeek(salon.getId(), startDate);
            } catch (Exception e) {
                log.error("Error generating slots for salon {}: {}", salon.getId(), e.getMessage());
            }
        }
        
        log.info("Completed generating slots for all salons");
    }
    
    /**
     * Mark a time slot as unavailable
     * @param timeSlotId Time slot ID
     */
    @Transactional
    public void markSlotAsUnavailable(String timeSlotId) {
        log.info("Marking slot {} as unavailable", timeSlotId);
        
        TimeSlot slot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + timeSlotId));
        
        slot.setIsAvailable(false);
        timeSlotRepository.save(slot);
        
        log.info("Slot {} marked as unavailable", timeSlotId);
    }
    
    /**
     * Mark a time slot as available
     * @param timeSlotId Time slot ID
     */
    @Transactional
    public void markSlotAsAvailable(String timeSlotId) {
        log.info("Marking slot {} as available", timeSlotId);
        
        TimeSlot slot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + timeSlotId));
        
        slot.setIsAvailable(true);
        timeSlotRepository.save(slot);
        
        log.info("Slot {} marked as available", timeSlotId);
    }
    
    /**
     * Get time slot by ID
     * @param id Time slot ID
     * @return TimeSlot entity
     */
    public TimeSlot getTimeSlotById(String id) {
        log.info("Fetching time slot with ID: {}", id);
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + id));
    }
    
    /**
     * Verify if a time slot is available for booking
     * @param timeSlotId Time slot ID
     * @throws TimeSlotNotAvailableException if slot is not available
     */
    public void verifySlotAvailability(String timeSlotId) {
        TimeSlot slot = getTimeSlotById(timeSlotId);
        
        if (!slot.getIsAvailable()) {
            throw new TimeSlotNotAvailableException("Time slot is not available for booking");
        }
        
        if (slot.isPast()) {
            throw new TimeSlotNotAvailableException("Cannot book a time slot in the past");
        }
    }
    
    /**
     * Convert TimeSlot entity to DTO
     * @param slot TimeSlot entity
     * @return TimeSlotDTO
     */
    public TimeSlotDTO convertToDTO(TimeSlot slot) {
        return TimeSlotDTO.builder()
                .id(slot.getId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .isAvailable(slot.getIsAvailable())
                .salonId(slot.getSalon().getId())
                .salonName(slot.getSalon().getName())
                .durationMinutes(slot.getDurationMinutes())
                .isPast(slot.isPast())
                .build();
    }
    
    /**
     * Parse time string to LocalTime
     * @param timeStr Time string (HH:mm format)
     * @param defaultTime Default time if parsing fails
     * @return LocalTime
     */
    private LocalTime parseTime(String timeStr, LocalTime defaultTime) {
        try {
            if (timeStr == null || timeStr.isEmpty()) {
                return defaultTime;
            }
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            log.warn("Failed to parse time: {}, using default: {}", timeStr, defaultTime);
            return defaultTime;
        }
    }
}
