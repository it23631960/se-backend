package com.example.salon_booking.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.salon_booking.dto.BusyHourResult;
import com.example.salon_booking.dto.DailyStatsResult;
import com.example.salon_booking.dto.ServicePopularityResult;
import com.example.salon_booking.dto.StatusCountResult;
import com.example.salon_booking.models.Appointment;
import com.example.salon_booking.models.AppointmentStatus;

/**
 * Enhanced Repository interface for Appointment entity
 * Provides comprehensive database operations for appointment management
 * Supports owner dashboard features: filtering, searching, statistics, reporting
 * 
 * @author Salon Booking System
 * @version 2.0
 */
@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    
    // ==================== BASIC CRUD & LOOKUP ====================
    
    /**
     * Find appointment by unique appointment number
     * @param appointmentNumber Appointment number (e.g., "APT00001")
     * @return Optional containing appointment if found
     */
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);
    
    /**
     * Find appointment by confirmation code
     * @param confirmationCode Confirmation code
     * @return Optional containing appointment if found
     */
    Optional<Appointment> findByConfirmationCode(String confirmationCode);
    
    /**
     * Check if appointment number already exists
     * @param appointmentNumber Appointment number
     * @return true if exists
     */
    Boolean existsByAppointmentNumber(String appointmentNumber);
    
    // ==================== SALON-BASED QUERIES (Owner Dashboard) ====================
    
    /**
     * Find all appointments for a specific salon
     * @param salonId Salon ID
     * @return List of appointments
     */
    List<Appointment> findBySalonId(String salonId);
    
    /**
     * Find salon appointments with pagination
     * @param salonId Salon ID
     * @param pageable Pagination parameters
     * @return Page of appointments
     */
    Page<Appointment> findBySalonId(String salonId, Pageable pageable);
    
    /**
     * Find appointments by salon and status
     * @param salonId Salon ID
     * @param status Appointment status
     * @return List of matching appointments
     */
    List<Appointment> findBySalonIdAndStatus(String salonId, AppointmentStatus status);
    
    /**
     * Find appointments by salon and multiple statuses
     * @param salonId Salon ID
     * @param statuses List of appointment statuses
     * @return List of matching appointments
     */
    List<Appointment> findBySalonIdAndStatusIn(String salonId, List<AppointmentStatus> statuses);
    
    /**
     * Count appointments by salon and status
     * @param salonId Salon ID
     * @param status Appointment status
     * @return Number of appointments
     */
    Long countBySalonIdAndStatus(String salonId, AppointmentStatus status);
    
    // ==================== DATE-BASED QUERIES ====================
    
    /**
     * Find appointments by salon and booking date range
     * @param salonId Salon ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of appointments
     */
    List<Appointment> findBySalonIdAndBookingDateBetween(
        String salonId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    /**
     * Find appointments for today (by salon)
     * Uses compound query on timeSlot.date
     * @param salonId Salon ID
     * @param date Today's date
     * @return List of today's appointments
     */
    @Query("{ 'salon.$id': ObjectId(?0), 'timeSlot.date': ?1 }")
    List<Appointment> findTodaysAppointments(String salonId, LocalDate date);
    
    /**
     * Count today's appointments for a salon
     * @param salonId Salon ID
     * @param date Today's date
     * @return Count of appointments
     */
    @Query(value = "{ 'salon.$id': ObjectId(?0), 'timeSlot.date': ?1 }", count = true)
    Long countTodaysAppointments(String salonId, LocalDate date);
    
    /**
     * Find appointments by salon and date range with status filter
     * @param salonId Salon ID
     * @param startDate Start date
     * @param endDate End date
     * @param status Appointment status
     * @return List of appointments
     */
    @Query("{ 'salon.$id': ObjectId(?0), 'timeSlot.date': { $gte: ?1, $lte: ?2 }, 'status': ?3 }")
    List<Appointment> findByDateRangeAndStatus(
        String salonId, 
        LocalDate startDate, 
        LocalDate endDate, 
        AppointmentStatus status
    );
    
    // ==================== CUSTOMER-BASED QUERIES ====================
    
    /**
     * Find all appointments for a specific customer
     * @param customerId Customer ID
     * @return List of appointments
     */
    List<Appointment> findByCustomerId(String customerId);
    
    /**
     * Find customer appointments ordered by booking date (newest first)
     * @param customerId Customer ID
     * @return List of appointments ordered by date
     */
    List<Appointment> findByCustomerIdOrderByBookingDateDesc(String customerId);
    
    /**
     * Find appointments by salon and customer
     * @param salonId Salon ID
     * @param customerId Customer ID
     * @return List of appointments
     */
    List<Appointment> findBySalonIdAndCustomerId(String salonId, String customerId);
    
    // ==================== SERVICE-BASED QUERIES ====================
    
    /**
     * Find appointments by salon and service
     * @param salonId Salon ID
     * @param serviceId Service ID
     * @return List of appointments
     */
    List<Appointment> findBySalonIdAndServiceId(String salonId, String serviceId);
    
    /**
     * Count appointments by salon and service
     * @param salonId Salon ID
     * @param serviceId Service ID
     * @return Number of appointments for this service
     */
    Long countBySalonIdAndServiceId(String salonId, String serviceId);
    
    // ==================== TIME SLOT QUERIES ====================
    
    /**
     * Find appointment by time slot (excluding cancelled)
     * Used to check if slot is already booked
     * @param timeSlotId Time slot ID
     * @param status Status to exclude (typically CANCELLED)
     * @return Optional containing appointment if slot is booked
     */
    Optional<Appointment> findByTimeSlotIdAndStatusNot(String timeSlotId, AppointmentStatus status);
    
    /**
     * Check if time slot is already booked (not cancelled)
     * @param timeSlotId Time slot ID
     * @param status Status to exclude
     * @return true if time slot is booked
     */
    Boolean existsByTimeSlotIdAndStatusNot(String timeSlotId, AppointmentStatus status);
    
    /**
     * Find all appointments for a specific time slot
     * @param timeSlotId Time slot ID
     * @return List of appointments (should be 0 or 1 normally)
     */
    List<Appointment> findByTimeSlotId(String timeSlotId);
    
    // ==================== SEARCH & FILTER ====================
    
    /**
     * Search appointments by customer name or phone
     * Case-insensitive search across customer.name and customer.phone fields
     * @param salonId Salon ID
     * @param searchTerm Search term (name or phone)
     * @return List of matching appointments
     */
    @Query("{ 'salon.$id': ObjectId(?0), $or: [ " +
           "{ 'customer.name': { $regex: ?1, $options: 'i' } }, " +
           "{ 'customer.phone': { $regex: ?1, $options: 'i' } } ] }")
    List<Appointment> searchByCustomerNameOrPhone(String salonId, String searchTerm);
    
    /**
     * Advanced search with multiple filters
     * @param salonId Salon ID
     * @param searchTerm Search term (optional)
     * @param status Status filter (optional)
     * @param startDate Start date filter (optional)
     * @param endDate End date filter (optional)
     * @return List of matching appointments
     */
    @Query("{ 'salon.$id': ObjectId(?0), " +
           "$and: [ " +
           "{ $or: [ " +
           "  { 'customer.name': { $regex: ?1, $options: 'i' } }, " +
           "  { 'customer.phone': { $regex: ?1, $options: 'i' } }, " +
           "  { 'appointmentNumber': { $regex: ?1, $options: 'i' } } " +
           "] }, " +
           "{ $or: [ { 'status': ?2 }, { ?2: null } ] }, " +
           "{ $or: [ { 'timeSlot.date': { $gte: ?3 } }, { ?3: null } ] }, " +
           "{ $or: [ { 'timeSlot.date': { $lte: ?4 } }, { ?4: null } ] } " +
           "] }")
    List<Appointment> advancedSearch(
        String salonId, 
        String searchTerm, 
        AppointmentStatus status, 
        LocalDate startDate, 
        LocalDate endDate
    );
    
    // ==================== GENERAL QUERIES ====================
    
    /**
     * Find appointments by status
     * @param status Appointment status
     * @return List of appointments with given status
     */
    List<Appointment> findByStatus(AppointmentStatus status);
    
    /**
     * Find appointments booked between dates (all salons)
     * @param startDate Start date
     * @param endDate End date
     * @return List of appointments
     */
    List<Appointment> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count appointments by status (all salons)
     * @param status Appointment status
     * @return Number of appointments
     */
    Long countByStatus(AppointmentStatus status);
    
    // ==================== STATISTICS & AGGREGATION ====================
    
    /**
     * Calculate total revenue for salon in date range
     * Only includes COMPLETED appointments with PAID status
     * @param salonId Salon ID
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue amount
     */
    @Aggregation(pipeline = {
        "{ '$match': { " +
        "  'salon.$id': ObjectId(?0), " +
        "  'status': 'COMPLETED', " +
        "  'paymentStatus': 'PAID', " +
        "  'timeSlot.date': { $gte: ?1, $lte: ?2 } " +
        "} }",
        "{ '$group': { " +
        "  '_id': null, " +
        "  'totalRevenue': { '$sum': '$totalAmount' } " +
        "} }"
    })
    Double calculateRevenue(String salonId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get popular services by booking count
     * Returns top services with booking counts
     * @param salonId Salon ID
     * @param limit Maximum number of services to return
     * @return List of service IDs with booking counts
     */
    @Aggregation(pipeline = {
        "{ '$match': { " +
        "  'salon.$id': ObjectId(?0), " +
        "  'status': { $in: ['CONFIRMED', 'COMPLETED'] } " +
        "} }",
        "{ '$group': { " +
        "  '_id': '$service.$id', " +
        "  'bookingCount': { '$sum': 1 }, " +
        "  'revenue': { '$sum': '$totalAmount' } " +
        "} }",
        "{ '$sort': { 'bookingCount': -1 } }",
        "{ '$limit': ?1 }"
    })
    List<ServicePopularityResult> findPopularServices(String salonId, int limit);
    
    /**
     * Find busiest hours of the day
     * Groups appointments by hour and counts bookings
     * @param salonId Salon ID
     * @return List of hours with booking counts
     */
    @Aggregation(pipeline = {
        "{ '$match': { 'salon.$id': ObjectId(?0) } }",
        "{ '$group': { " +
        "  '_id': { $hour: '$timeSlot.startTime' }, " +
        "  'bookingCount': { '$sum': 1 } " +
        "} }",
        "{ '$sort': { 'bookingCount': -1 } }"
    })
    List<BusyHourResult> findBusiestHours(String salonId);
    
    /**
     * Get appointment statistics by status for salon
     * @param salonId Salon ID
     * @return List of status counts
     */
    @Aggregation(pipeline = {
        "{ '$match': { 'salon.$id': ObjectId(?0) } }",
        "{ '$group': { " +
        "  '_id': '$status', " +
        "  'count': { '$sum': 1 } " +
        "} }"
    })
    List<StatusCountResult> getStatusStatistics(String salonId);
    
    /**
     * Get daily appointment counts for date range
     * Useful for charts and graphs
     * @param salonId Salon ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of daily counts
     */
    @Aggregation(pipeline = {
        "{ '$match': { " +
        "  'salon.$id': ObjectId(?0), " +
        "  'timeSlot.date': { $gte: ?1, $lte: ?2 } " +
        "} }",
        "{ '$group': { " +
        "  '_id': '$timeSlot.date', " +
        "  'appointmentCount': { '$sum': 1 }, " +
        "  'revenue': { '$sum': '$totalAmount' } " +
        "} }",
        "{ '$sort': { '_id': 1 } }"
    })
    List<DailyStatsResult> getDailyStatistics(String salonId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Count first-time customers
     * @param salonId Salon ID
     * @return Number of first-time customer appointments
     */
    Long countBySalonIdAndIsFirstTimeTrue(String salonId);
    
    /**
     * Find appointments pending confirmation (for salon dashboard alerts)
     * @param salonId Salon ID
     * @return List of pending appointments
     */
    List<Appointment> findBySalonIdAndStatusOrderByBookingDateAsc(
        String salonId, 
        AppointmentStatus status
    );
    
    /**
     * Find upcoming appointments (confirmed, date >= today)
     * @param salonId Salon ID
     * @param today Today's date
     * @return List of upcoming appointments
     */
    @Query("{ 'salon.$id': ObjectId(?0), 'status': 'CONFIRMED', 'timeSlot.date': { $gte: ?1 } }")
    List<Appointment> findUpcomingAppointments(String salonId, LocalDate today);
    
    /**
     * Find appointments that need reminders
     * Confirmed appointments for tomorrow where reminder not sent
     * @param salonId Salon ID
     * @param tomorrow Tomorrow's date
     * @return List of appointments needing reminders
     */
    @Query("{ 'salon.$id': ObjectId(?0), " +
           "'status': 'CONFIRMED', " +
           "'timeSlot.date': ?1, " +
           "'reminderSent': false }")
    List<Appointment> findAppointmentsNeedingReminders(String salonId, LocalDate tomorrow);
}
