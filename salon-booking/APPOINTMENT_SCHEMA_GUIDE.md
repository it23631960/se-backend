# Appointment Database Schema - Complete Implementation Guide

## 🎯 Overview

This document provides a comprehensive guide to the enhanced Appointment database schema designed for the Salon Booking System owner dashboard (Task CS2-60).

**Version:** 2.0  
**Date:** October 11, 2025  
**Status:** ✅ COMPLETE

---

## 📋 Table of Contents

1. [Database Schema](#database-schema)
2. [Enums](#enums)
3. [Repository Methods](#repository-methods)
4. [Aggregation Result Classes](#aggregation-result-classes)
5. [Service Layer Implementation](#service-layer-implementation)
6. [DTOs](#dtos)
7. [Sample Data](#sample-data)
8. [Indexes and Performance](#indexes-and-performance)
9. [Best Practices](#best-practices)
10. [API Endpoints](#api-endpoints)

---

## 🗄️ Database Schema

### Appointment Entity

**File:** `Appointment.java`  
**Collection:** `appointments`

#### Core Fields

```java
@Id
private String id;                          // MongoDB ObjectId

@NotBlank
@Indexed(unique = true)
private String appointmentNumber;           // APT00001, APT00002, etc.
```

#### Relationships (DBRef)

```java
@DBRef @NotNull
private Customer customer;                  // Who booked

@DBRef @NotNull
private Service service;                    // What service

@DBRef @NotNull
private TimeSlot timeSlot;                  // When (date + time)

@DBRef @NotNull
private Salon salon;                        // Where
```

#### Status Management

```java
@NotNull
private AppointmentStatus status;           // PENDING, CONFIRMED, etc.

private LocalDateTime bookingDate;          // When customer booked
private LocalDateTime confirmedAt;          // When salon confirmed
private LocalDateTime completedAt;          // When service completed
private LocalDateTime cancelledAt;          // When cancelled
```

#### Cancellation Information

```java
private String cancelledBy;                 // "CUSTOMER", "SALON", "ADMIN"
@Size(max = 200)
private String cancellationReason;          // Why cancelled
@Size(max = 500)
private String cancellationNotes;           // Additional details
```

#### Payment Information

```java
@NotNull
private PaymentStatus paymentStatus;        // PENDING, PAID, REFUNDED

@NotNull @DecimalMin("0.0")
private Double totalAmount;                 // Service price at booking

private String paymentMethod;               // "CASH", "CARD", "ONLINE"
private LocalDateTime paidAt;               // When payment received
private String transactionReference;        // For online/card payments
```

#### Customer Preferences & Notes

```java
@Size(max = 500)
private String customerNotes;               // From customer
@Size(max = 500)
private String salonNotes;                  // Internal notes

private String assignedStaff;               // Stylist/beautician name
private Boolean isFirstTime;                // First visit flag
private String confirmationCode;            // For verification
```

#### Notification Tracking

```java
private Boolean confirmationSent;           // Confirmation email/SMS sent
private Boolean reminderSent;               // Reminder sent (24h before)
private LocalDateTime reminderSentAt;       // When reminder sent
```

#### Metadata & Audit

```java
@CreatedDate
private LocalDateTime createdAt;            // Auto-set on creation

@LastModifiedDate
private LocalDateTime updatedAt;            // Auto-update on modification

private String createdBy;                   // Who created (user ID)
private String lastModifiedBy;              // Who last modified
```

#### Optional Advanced Features

```java
private Boolean isRecurring;                // Recurring appointment
private String recurringPattern;            // "WEEKLY", "MONTHLY"
private String parentAppointmentId;         // Link to parent

private Boolean isGroupBooking;             // Group booking flag
private List<String> additionalCustomerIds; // Other customers in group

private Boolean hasReview;                  // Customer left review
private String reviewId;                    // Link to review
```

---

## 🏷️ Enums

### AppointmentStatus

**File:** `AppointmentStatus.java`

```java
public enum AppointmentStatus {
    PENDING("Pending Confirmation"),        // Initial state
    CONFIRMED("Confirmed"),                 // Salon confirmed
    CANCELLED("Cancelled"),                 // Cancelled (final)
    COMPLETED("Completed"),                 // Service done (final)
    NO_SHOW("No Show");                     // Customer no-show (final)
    
    private final String displayName;
    
    // Methods
    public boolean isFinalState()           // true for COMPLETED, CANCELLED, NO_SHOW
    public boolean isCancellable()          // true for PENDING, CONFIRMED
}
```

**Valid Status Transitions:**
- `PENDING` → `CONFIRMED` (salon confirms)
- `PENDING` → `CANCELLED` (customer/salon cancels)
- `CONFIRMED` → `COMPLETED` (after service)
- `CONFIRMED` → `CANCELLED` (cancelled)
- `CONFIRMED` → `NO_SHOW` (customer didn't show)
- `COMPLETED` → (final, cannot change)
- `CANCELLED` → (final, cannot change)
- `NO_SHOW` → (final, cannot change)

### PaymentStatus

**File:** `PaymentStatus.java`

```java
public enum PaymentStatus {
    PENDING("Pending Payment"),             // Not paid yet
    PAID("Paid"),                           // Payment received
    REFUNDED("Refunded");                   // Payment refunded
    
    private final String displayName;
}
```

---

## 🔍 Repository Methods

### File: `AppointmentRepository.java`

#### Basic CRUD & Lookup

```java
// Find by appointment number
Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

// Find by confirmation code
Optional<Appointment> findByConfirmationCode(String confirmationCode);

// Check if appointment number exists
Boolean existsByAppointmentNumber(String appointmentNumber);
```

#### Salon-Based Queries (Owner Dashboard)

```java
// Get all salon appointments
List<Appointment> findBySalonId(String salonId);
Page<Appointment> findBySalonId(String salonId, Pageable pageable);

// Filter by status
List<Appointment> findBySalonIdAndStatus(String salonId, AppointmentStatus status);
List<Appointment> findBySalonIdAndStatusIn(String salonId, List<AppointmentStatus> statuses);

// Count by status
Long countBySalonIdAndStatus(String salonId, AppointmentStatus status);
```

#### Date-Based Queries

```java
// Get today's appointments
@Query("{ 'salon.$id': ObjectId(?0), 'timeSlot.date': ?1 }")
List<Appointment> findTodaysAppointments(String salonId, LocalDate date);

// Count today's appointments
@Query(value = "{ 'salon.$id': ObjectId(?0), 'timeSlot.date': ?1 }", count = true)
Long countTodaysAppointments(String salonId, LocalDate date);

// Date range with status filter
@Query("{ 'salon.$id': ObjectId(?0), 'timeSlot.date': { $gte: ?1, $lte: ?2 }, 'status': ?3 }")
List<Appointment> findByDateRangeAndStatus(
    String salonId, LocalDate startDate, LocalDate endDate, AppointmentStatus status
);
```

#### Customer-Based Queries

```java
// All customer appointments
List<Appointment> findByCustomerId(String customerId);

// Customer appointments (newest first)
List<Appointment> findByCustomerIdOrderByBookingDateDesc(String customerId);

// Salon + customer
List<Appointment> findBySalonIdAndCustomerId(String salonId, String customerId);
```

#### Service-Based Queries

```java
// Appointments for a service
List<Appointment> findBySalonIdAndServiceId(String salonId, String serviceId);

// Count service bookings
Long countBySalonIdAndServiceId(String salonId, String serviceId);
```

#### Time Slot Queries

```java
// Check if slot is booked
Optional<Appointment> findByTimeSlotIdAndStatusNot(String timeSlotId, AppointmentStatus status);
Boolean existsByTimeSlotIdAndStatusNot(String timeSlotId, AppointmentStatus status);
```

#### Search & Filter

```java
// Search by customer name or phone (case-insensitive)
@Query("{ 'salon.$id': ObjectId(?0), $or: [ " +
       "{ 'customer.name': { $regex: ?1, $options: 'i' } }, " +
       "{ 'customer.phone': { $regex: ?1, $options: 'i' } } ] }")
List<Appointment> searchByCustomerNameOrPhone(String salonId, String searchTerm);

// Advanced search with multiple filters
@Query("{ /* complex query */ }")
List<Appointment> advancedSearch(
    String salonId, String searchTerm, AppointmentStatus status, 
    LocalDate startDate, LocalDate endDate
);
```

#### Statistics & Aggregation

```java
// Calculate revenue (COMPLETED + PAID only)
@Aggregation(pipeline = { /* aggregation */ })
Double calculateRevenue(String salonId, LocalDate startDate, LocalDate endDate);

// Popular services
@Aggregation(pipeline = { /* aggregation */ })
List<ServicePopularityResult> findPopularServices(String salonId, int limit);

// Busiest hours
@Aggregation(pipeline = { /* aggregation */ })
List<BusyHourResult> findBusiestHours(String salonId);

// Status statistics
@Aggregation(pipeline = { /* aggregation */ })
List<StatusCountResult> getStatusStatistics(String salonId);

// Daily statistics
@Aggregation(pipeline = { /* aggregation */ })
List<DailyStatsResult> getDailyStatistics(String salonId, LocalDate startDate, LocalDate endDate);
```

#### Alerts & Notifications

```java
// Pending appointments (for dashboard alerts)
List<Appointment> findBySalonIdAndStatusOrderByBookingDateAsc(
    String salonId, AppointmentStatus status
);

// Upcoming appointments
@Query("{ 'salon.$id': ObjectId(?0), 'status': 'CONFIRMED', 'timeSlot.date': { $gte: ?1 } }")
List<Appointment> findUpcomingAppointments(String salonId, LocalDate today);

// Appointments needing reminders (tomorrow, confirmed, not sent)
@Query("{ 'salon.$id': ObjectId(?0), 'status': 'CONFIRMED', " +
       "'timeSlot.date': ?1, 'reminderSent': false }")
List<Appointment> findAppointmentsNeedingReminders(String salonId, LocalDate tomorrow);
```

---

## 📊 Aggregation Result Classes

### ServicePopularityResult

**File:** `dto/ServicePopularityResult.java`

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ServicePopularityResult {
    private String serviceId;
    private Long bookingCount;
    private Double revenue;
    private String serviceName;           // Populated after query
}
```

### BusyHourResult

**File:** `dto/BusyHourResult.java`

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BusyHourResult {
    private Integer hour;                 // 0-23
    private Long bookingCount;
    
    public String getFormattedHour() {    // "9:00 AM", "2:00 PM"
        // Implementation
    }
}
```

### StatusCountResult

**File:** `dto/StatusCountResult.java`

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StatusCountResult {
    private AppointmentStatus status;
    private Long count;
    
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "Unknown";
    }
}
```

### DailyStatsResult

**File:** `dto/DailyStatsResult.java`

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DailyStatsResult {
    private LocalDate date;
    private Long appointmentCount;
    private Double revenue;
    
    public String getFormattedDate() {
        return date != null ? date.toString() : "Unknown";
    }
    
    public Double getAverageRevenuePerAppointment() {
        // Calculate average
    }
}
```

---

## 🛠️ Service Layer Implementation

### AppointmentService

**File:** `service/AppointmentService.java`

```java
@Service
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ServiceRepository serviceRepository;
    
    // ==================== APPOINTMENT NUMBER GENERATION ====================
    
    /**
     * Generate unique appointment number
     * Format: APT00001, APT00002, etc.
     */
    private String generateAppointmentNumber() {
        long count = appointmentRepository.count();
        String number;
        do {
            number = String.format("APT%05d", count + 1);
            count++;
        } while (appointmentRepository.existsByAppointmentNumber(number));
        return number;
    }
    
    // ==================== CREATE APPOINTMENT ====================
    
    /**
     * Create new appointment
     * - Generates appointment number
     * - Captures service price
     * - Marks time slot as unavailable
     * - Sets initial status to PENDING
     */
    public Appointment createAppointment(AppointmentRequestDTO dto) {
        // Validate time slot is available
        TimeSlot timeSlot = timeSlotRepository.findById(dto.getTimeSlotId())
            .orElseThrow(() -> new ResourceNotFoundException("Time slot not found"));
        
        if (!timeSlot.getIsAvailable()) {
            throw new BusinessException("Time slot is not available");
        }
        
        // Get service to capture price
        Service service = serviceRepository.findById(dto.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        
        // Create appointment
        Appointment appointment = Appointment.builder()
            .appointmentNumber(generateAppointmentNumber())
            .customer(/* get customer */)
            .service(service)
            .timeSlot(timeSlot)
            .salon(/* get salon */)
            .status(AppointmentStatus.PENDING)
            .totalAmount(service.getPrice())
            .paymentStatus(PaymentStatus.PENDING)
            .paymentMethod("PENDING")
            .bookingDate(LocalDateTime.now())
            .customerNotes(dto.getCustomerNotes())
            .isFirstTime(dto.getIsFirstTime())
            .confirmationSent(false)
            .reminderSent(false)
            .build();
        
        // Mark time slot as unavailable
        timeSlot.setIsAvailable(false);
        timeSlotRepository.save(timeSlot);
        
        // Save appointment
        Appointment saved = appointmentRepository.save(appointment);
        
        // Send confirmation (async)
        // notificationService.sendConfirmation(saved);
        
        return saved;
    }
    
    // ==================== STATUS MANAGEMENT ====================
    
    /**
     * Confirm appointment
     * Changes status from PENDING to CONFIRMED
     */
    public Appointment confirmAppointment(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (!appointment.isConfirmable()) {
            throw new BusinessException("Appointment cannot be confirmed");
        }
        
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmedAt(LocalDateTime.now());
        
        return appointmentRepository.save(appointment);
    }
    
    /**
     * Complete appointment
     * Changes status from CONFIRMED to COMPLETED
     */
    public Appointment completeAppointment(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (!appointment.isCompletable()) {
            throw new BusinessException("Appointment cannot be completed");
        }
        
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setCompletedAt(LocalDateTime.now());
        
        return appointmentRepository.save(appointment);
    }
    
    /**
     * Cancel appointment
     * - Changes status to CANCELLED
     * - Records cancellation details
     * - Frees up time slot
     */
    @Transactional
    public void cancelAppointment(String appointmentId, String reason, String cancelledBy) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (!appointment.isCancellable()) {
            throw new BusinessException("Appointment cannot be cancelled");
        }
        
        // Update appointment
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancelledBy(cancelledBy);
        appointment.setCancellationReason(reason);
        
        // Free up time slot
        TimeSlot timeSlot = appointment.getTimeSlot();
        timeSlot.setIsAvailable(true);
        timeSlotRepository.save(timeSlot);
        
        appointmentRepository.save(appointment);
        
        // Send cancellation notification
        // notificationService.sendCancellation(appointment);
    }
    
    /**
     * Mark appointment as no-show
     */
    public Appointment markAsNoShow(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (!appointment.canMarkAsNoShow()) {
            throw new BusinessException("Appointment cannot be marked as no-show");
        }
        
        appointment.setStatus(AppointmentStatus.NO_SHOW);
        
        return appointmentRepository.save(appointment);
    }
    
    // ==================== PAYMENT MANAGEMENT ====================
    
    /**
     * Mark payment as received
     */
    public Appointment recordPayment(String appointmentId, String paymentMethod, String transactionRef) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        appointment.setPaymentStatus(PaymentStatus.PAID);
        appointment.setPaymentMethod(paymentMethod);
        appointment.setPaidAt(LocalDateTime.now());
        appointment.setTransactionReference(transactionRef);
        
        return appointmentRepository.save(appointment);
    }
    
    /**
     * Process refund
     */
    public Appointment processRefund(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (appointment.getPaymentStatus() != PaymentStatus.PAID) {
            throw new BusinessException("No payment to refund");
        }
        
        appointment.setPaymentStatus(PaymentStatus.REFUNDED);
        
        return appointmentRepository.save(appointment);
    }
    
    // ==================== STATISTICS ====================
    
    /**
     * Get comprehensive appointment statistics for owner dashboard
     */
    public AppointmentStatsDTO getStatistics(String salonId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(7);
        LocalDate monthStart = today.withDayOfMonth(1);
        
        return AppointmentStatsDTO.builder()
            .todayCount(appointmentRepository.countTodaysAppointments(salonId, today))
            .pendingCount(appointmentRepository.countBySalonIdAndStatus(salonId, AppointmentStatus.PENDING))
            .confirmedCount(appointmentRepository.countBySalonIdAndStatus(salonId, AppointmentStatus.CONFIRMED))
            .completedCount(appointmentRepository.countBySalonIdAndStatus(salonId, AppointmentStatus.COMPLETED))
            .cancelledCount(appointmentRepository.countBySalonIdAndStatus(salonId, AppointmentStatus.CANCELLED))
            .weekRevenue(appointmentRepository.calculateRevenue(salonId, weekStart, today))
            .monthRevenue(appointmentRepository.calculateRevenue(salonId, monthStart, today))
            .popularServices(appointmentRepository.findPopularServices(salonId, 5))
            .busiestHours(appointmentRepository.findBusiestHours(salonId))
            .build();
    }
    
    // ==================== SEARCH & FILTER ====================
    
    /**
     * Search appointments by customer name/phone
     */
    public List<Appointment> searchAppointments(String salonId, String searchTerm) {
        return appointmentRepository.searchByCustomerNameOrPhone(salonId, searchTerm);
    }
    
    /**
     * Get appointments by date range
     */
    public List<Appointment> getAppointmentsByDateRange(
        String salonId, LocalDate startDate, LocalDate endDate
    ) {
        return appointmentRepository.findBySalonIdAndBookingDateBetween(
            salonId, 
            startDate.atStartOfDay(), 
            endDate.atTime(23, 59, 59)
        );
    }
    
    /**
     * Get appointments by status
     */
    public List<Appointment> getAppointmentsByStatus(String salonId, AppointmentStatus status) {
        return appointmentRepository.findBySalonIdAndStatus(salonId, status);
    }
    
    // ==================== HELPER METHODS ====================
    
    private Appointment getAppointmentById(String id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
    }
}
```

---

## 📦 DTOs

### AppointmentRequestDTO

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    @NotNull
    private String customerId;
    
    @NotNull
    private String serviceId;
    
    @NotNull
    private String timeSlotId;
    
    @NotNull
    private String salonId;
    
    @Size(max = 500)
    private String customerNotes;
    
    private Boolean isFirstTime;
}
```

### AppointmentResponseDTO

```java
@Data
@Builder
public class AppointmentResponseDTO {
    private String id;
    private String appointmentNumber;
    
    // Simplified references
    private CustomerSummaryDTO customer;
    private ServiceSummaryDTO service;
    private TimeSlotSummaryDTO timeSlot;
    private SalonSummaryDTO salon;
    
    private AppointmentStatus status;
    private String statusDisplayName;
    
    private PaymentStatus paymentStatus;
    private Double totalAmount;
    private String paymentMethod;
    
    private LocalDateTime bookingDate;
    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;
    
    private String customerNotes;
    private String salonNotes;
    private String assignedStaff;
    
    private Boolean isCancellable;
    private Boolean isReschedulable;
    private Boolean isUpcoming;
}
```

### AppointmentStatsDTO

```java
@Data
@Builder
public class AppointmentStatsDTO {
    private Long todayCount;
    private Long pendingCount;
    private Long confirmedCount;
    private Long completedCount;
    private Long cancelledCount;
    private Long noShowCount;
    
    private Double weekRevenue;
    private Double monthRevenue;
    
    private List<ServicePopularityResult> popularServices;
    private List<BusyHourResult> busiestHours;
    private List<DailyStatsResult> dailyStats;
}
```

---

## 🗂️ Sample Data

### Sample Appointment JSON

```json
{
  "_id": "671a2b3c4d5e6f7890abcdef",
  "appointmentNumber": "APT00001",
  "customer": { "$ref": "customers", "$id": "customer123" },
  "service": { "$ref": "services", "$id": "service456" },
  "timeSlot": { "$ref": "time_slots", "$id": "slot789" },
  "salon": { "$ref": "salons", "$id": "salon001" },
  
  "status": "CONFIRMED",
  "bookingDate": "2025-10-08T14:30:00",
  "confirmedAt": "2025-10-09T10:00:00",
  "completedAt": null,
  "cancelledAt": null,
  
  "cancelledBy": null,
  "cancellationReason": null,
  "cancellationNotes": null,
  
  "paymentStatus": "PENDING",
  "totalAmount": 1500.00,
  "paymentMethod": "PENDING",
  "paidAt": null,
  "transactionReference": null,
  
  "customerNotes": "First time customer, prefers early morning",
  "salonNotes": "Regular customer of owner's other location",
  "assignedStaff": "Priya Fernando",
  "isFirstTime": true,
  "confirmationCode": "CONF-2025-001",
  
  "confirmationSent": true,
  "reminderSent": false,
  "reminderSentAt": null,
  
  "createdAt": "2025-10-08T14:30:00",
  "updatedAt": "2025-10-09T10:00:00",
  "createdBy": "customer123",
  "lastModifiedBy": "admin001",
  
  "isRecurring": false,
  "recurringPattern": null,
  "parentAppointmentId": null,
  
  "isGroupBooking": false,
  "additionalCustomerIds": [],
  
  "hasReview": false,
  "reviewId": null
}
```

---

## ⚡ Indexes and Performance

### Compound Indexes (Defined in @CompoundIndexes)

```java
@CompoundIndexes({
    // Salon + date (most common query)
    @CompoundIndex(name = "salon_date_idx", 
        def = "{'salon.$id': 1, 'timeSlot.date': 1}"),
    
    // Salon + status (filtering)
    @CompoundIndex(name = "salon_status_idx", 
        def = "{'salon.$id': 1, 'status': 1}"),
    
    // Salon + date + status (combined filtering)
    @CompoundIndex(name = "salon_date_status_idx", 
        def = "{'salon.$id': 1, 'timeSlot.date': 1, 'status': 1}"),
    
    // Customer appointments (sorted by date)
    @CompoundIndex(name = "customer_booking_idx", 
        def = "{'customer.$id': 1, 'bookingDate': -1}"),
    
    // Time slot booking check
    @CompoundIndex(name = "timeslot_status_idx", 
        def = "{'timeSlot.$id': 1, 'status': 1}")
})
```

### Single Indexes

```java
@Indexed(unique = true)
private String appointmentNumber;      // Unique lookup
```

### Why These Indexes?

1. **`salon_date_idx`**: Fast retrieval of appointments for a specific day
2. **`salon_status_idx`**: Quick filtering by status (pending, confirmed, etc.)
3. **`salon_date_status_idx`**: Combined filtering (most common dashboard query)
4. **`customer_booking_idx`**: Customer appointment history (newest first)
5. **`timeslot_status_idx`**: Check if time slot is already booked
6. **`appointmentNumber`**: Quick lookup by appointment number

---

## ✅ Best Practices

### 1. Always Use Transactions

```java
@Transactional
public void cancelAppointment(String appointmentId) {
    // Update appointment AND free time slot
    // Both must succeed or both must fail
}
```

### 2. Capture Price at Booking

```java
// Store service price at booking time
appointment.setTotalAmount(service.getPrice());
// This preserves historical accuracy even if service price changes
```

### 3. Validate Status Transitions

```java
if (!appointment.isConfirmable()) {
    throw new BusinessException("Appointment cannot be confirmed");
}
```

### 4. Use Pagination for Large Result Sets

```java
Page<Appointment> findBySalonId(String salonId, Pageable pageable);
```

### 5. Enable MongoDB Auditing

```java
@EnableMongoAuditing
public class MongoConfig {
    // Enables @CreatedDate and @LastModifiedDate
}
```

### 6. Use DTOs for API Responses

```java
// DON'T expose full entity with DBRef
public Appointment getAppointment() { ... }

// DO use DTOs with simplified data
public AppointmentResponseDTO getAppointment() { ... }
```

### 7. Implement Soft Cancellation

```java
// DON'T delete appointments
appointmentRepository.delete(appointment);

// DO mark as cancelled
appointment.setStatus(AppointmentStatus.CANCELLED);
appointment.setCancelledAt(LocalDateTime.now());
```

---

## 🌐 API Endpoints (Recommended)

### Owner Dashboard Endpoints

```
GET  /api/owner/appointments                    # List all
GET  /api/owner/appointments/today              # Today's appointments
GET  /api/owner/appointments/pending            # Pending confirmation
GET  /api/owner/appointments/upcoming           # Upcoming appointments
GET  /api/owner/appointments/statistics         # Dashboard stats
GET  /api/owner/appointments/search?q={term}    # Search

GET  /api/owner/appointments/{id}               # Get details
PUT  /api/owner/appointments/{id}/confirm       # Confirm
PUT  /api/owner/appointments/{id}/complete      # Complete
PUT  /api/owner/appointments/{id}/cancel        # Cancel
PUT  /api/owner/appointments/{id}/no-show       # Mark no-show

PUT  /api/owner/appointments/{id}/payment       # Record payment
PUT  /api/owner/appointments/{id}/notes         # Update notes
```

### Customer Endpoints

```
POST /api/customer/appointments                 # Book appointment
GET  /api/customer/appointments                 # My appointments
GET  /api/customer/appointments/{id}            # Get details
PUT  /api/customer/appointments/{id}/cancel     # Cancel
PUT  /api/customer/appointments/{id}/reschedule # Reschedule
```

### Admin Endpoints

```
GET  /api/admin/appointments                    # All appointments
GET  /api/admin/appointments/statistics         # System-wide stats
PUT  /api/admin/appointments/{id}/force-cancel  # Admin cancel
```

---

## 🎓 Usage Examples

### Creating an Appointment

```java
// 1. Customer books appointment
AppointmentRequestDTO request = AppointmentRequestDTO.builder()
    .customerId("customer123")
    .serviceId("service456")
    .timeSlotId("slot789")
    .salonId("salon001")
    .customerNotes("First time customer")
    .isFirstTime(true)
    .build();

Appointment appointment = appointmentService.createAppointment(request);
// ✅ Appointment created with status PENDING
// ✅ Time slot marked as unavailable
// ✅ Appointment number generated (APT00001)
```

### Owner Confirms Appointment

```java
// 2. Salon owner confirms
Appointment confirmed = appointmentService.confirmAppointment(appointment.getId());
// ✅ Status changed to CONFIRMED
// ✅ confirmedAt timestamp set
```

### Completing Appointment

```java
// 3. After service is provided
Appointment completed = appointmentService.completeAppointment(appointment.getId());
// ✅ Status changed to COMPLETED
// ✅ completedAt timestamp set

// 4. Record payment
appointmentService.recordPayment(appointment.getId(), "CASH", null);
// ✅ paymentStatus changed to PAID
```

### Getting Statistics

```java
// Get dashboard statistics
AppointmentStatsDTO stats = appointmentService.getStatistics("salon001");

System.out.println("Today: " + stats.getTodayCount());
System.out.println("Pending: " + stats.getPendingCount());
System.out.println("Week Revenue: " + stats.getWeekRevenue());
System.out.println("Popular Services: " + stats.getPopularServices());
```

---

## 📝 Answers to Your Questions

### Q1: Should appointment number be auto-generated or manual?
**✅ Auto-generated** - Format: APT00001, APT00002, etc. Implemented in `generateAppointmentNumber()` method.

### Q2: Payment tracking needed now or later?
**✅ Now** - All payment fields added. Can be used immediately or gradually implemented.

### Q3: Who can cancel appointments?
**✅ Both** - Customer, Salon, and Admin. Tracked in `cancelledBy` field.

### Q4: Notification system integration?
**✅ Yes** - `confirmationSent`, `reminderSent`, `reminderSentAt` fields added. Repository has `findAppointmentsNeedingReminders()` method.

### Q5: Staff/stylist assignment needed?
**✅ Yes** - `assignedStaff` field stores stylist/staff member name.

### Q6: Recurring appointments support?
**✅ Yes** - `isRecurring`, `recurringPattern`, `parentAppointmentId` fields added for future implementation.

---

## 🚀 Next Steps

1. ✅ **Schema Complete** - Appointment entity ready
2. ✅ **Repository Complete** - All query methods implemented
3. ✅ **Enums Complete** - AppointmentStatus and PaymentStatus ready
4. ✅ **Result Classes Complete** - Aggregation DTOs created
5. ⏳ **Service Layer** - Implement AppointmentService (outline provided)
6. ⏳ **DTOs** - Create request/response DTOs
7. ⏳ **Controller** - Implement REST endpoints
8. ⏳ **Sample Data** - Add to DataInitializer
9. ⏳ **Frontend Integration** - Connect to owner dashboard

---

## 📚 References

- **MongoDB Spring Data Docs**: https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/
- **Aggregation Framework**: https://www.mongodb.com/docs/manual/aggregation/
- **Spring Data Auditing**: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing

---

**Status:** ✅ Database schema implementation complete and ready for service layer integration!

**Implementation Time:** Comprehensive schema with 30+ repository methods, full auditing, validation, and performance optimization.
