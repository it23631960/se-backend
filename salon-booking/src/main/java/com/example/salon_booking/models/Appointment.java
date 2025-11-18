package com.example.salon_booking.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enhanced Appointment entity representing a booked salon appointment
 * Supports comprehensive owner dashboard features including:
 * - Status tracking and transitions
 * - Payment management
 * - Cancellation handling
 * - Customer notes and preferences
 * - Staff assignment
 * - Notification tracking
 * - Revenue reporting
 * 
 * Links customer, service, time slot, and salon together with full audit trail
 * 
 * @author Salon Booking System
 * @version 2.0
 */
@Document(collection = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "salon_date_idx", def = "{'salon.$id': 1, 'timeSlot.date': 1}"),
    @CompoundIndex(name = "salon_status_idx", def = "{'salon.$id': 1, 'status': 1}"),
    @CompoundIndex(name = "salon_date_status_idx", def = "{'salon.$id': 1, 'timeSlot.date': 1, 'status': 1}"),
    @CompoundIndex(name = "customer_booking_idx", def = "{'customer.$id': 1, 'bookingDate': -1}"),
    @CompoundIndex(name = "timeslot_status_idx", def = "{'timeSlot.$id': 1, 'status': 1}")
})
public class Appointment {
    
    // ==================== CORE IDENTIFICATION ====================
    
    /**
     * Unique identifier for the appointment (MongoDB ObjectId)
     */
    @Id
    private String id;
    
    /**
     * Human-readable appointment number for easy reference
     * Format: APT00001, APT00002, etc.
     * Auto-generated on creation
     */
    @NotBlank(message = "Appointment number is required")
    @Indexed(unique = true)
    private String appointmentNumber;
    
    // ==================== RELATIONSHIPS ====================
    
    /**
     * Reference to the customer who made the booking
     */
    @DBRef
    @NotNull(message = "Customer is required")
    private Customer customer;
    
    /**
     * Reference to the service being booked
     */
    @DBRef
    @NotNull(message = "Service is required")
    private Service service;
    
    /**
     * Reference to the booked time slot
     */
    @DBRef
    @NotNull(message = "Time slot is required")
    private TimeSlot timeSlot;
    
    /**
     * Reference to the salon where appointment is scheduled
     */
    @DBRef
    @NotNull(message = "Salon is required")
    private Salon salon;
    
    // ==================== STATUS MANAGEMENT ====================
    
    /**
     * Current status of the appointment
     * Default: PENDING (awaiting salon confirmation)
     */
    @NotNull(message = "Status is required")
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;
    
    /**
     * Timestamp when the appointment was initially booked by customer
     * Set automatically on creation
     */
    @Builder.Default
    private LocalDateTime bookingDate = LocalDateTime.now();
    
    /**
     * Timestamp when salon confirmed the appointment
     * Set when status changes from PENDING to CONFIRMED
     */
    private LocalDateTime confirmedAt;
    
    /**
     * Timestamp when the service was completed
     * Set when status changes to COMPLETED
     */
    private LocalDateTime completedAt;
    
    /**
     * Timestamp when appointment was cancelled
     * Set when status changes to CANCELLED
     */
    private LocalDateTime cancelledAt;
    
    // ==================== CANCELLATION INFORMATION ====================
    
    /**
     * Who cancelled the appointment: "CUSTOMER", "SALON", "ADMIN", "SYSTEM"
     */
    private String cancelledBy;
    
    /**
     * Reason for cancellation (predefined or custom)
     * Examples: "Customer request", "Salon closed", "Emergency", "No show"
     */
    @Size(max = 200, message = "Cancellation reason cannot exceed 200 characters")
    private String cancellationReason;
    
    /**
     * Additional notes about the cancellation
     */
    @Size(max = 500, message = "Cancellation notes cannot exceed 500 characters")
    private String cancellationNotes;
    
    // ==================== PAYMENT INFORMATION ====================
    
    /**
     * Payment status tracking
     * Default: PENDING (pay on arrival or awaiting payment)
     */
    @NotNull(message = "Payment status is required")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    /**
     * Total amount for the service at the time of booking
     * Captured from service price to maintain historical accuracy
     */
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", message = "Total amount must be positive")
    private Double totalAmount;
    
    /**
     * Payment method used or expected
     * Values: "CASH", "CARD", "ONLINE", "BANK_TRANSFER", "PENDING"
     */
    @Builder.Default
    private String paymentMethod = "PENDING";
    
    /**
     * Timestamp when payment was received
     * Set when paymentStatus changes to PAID
     */
    private LocalDateTime paidAt;
    
    /**
     * Transaction reference number (for online/card payments)
     */
    private String transactionReference;
    
    // ==================== CUSTOMER PREFERENCES & NOTES ====================
    
    /**
     * Notes or special requests from customer during booking
     * Examples: "First time customer", "Allergic to certain products"
     */
    @Size(max = 500, message = "Customer notes cannot exceed 500 characters")
    private String customerNotes;
    
    /**
     * Internal notes by salon staff
     * Examples: "Regular customer", "Prefers stylist John"
     */
    @Size(max = 500, message = "Salon notes cannot exceed 500 characters")
    private String salonNotes;
    
    /**
     * Name of staff member assigned to this appointment
     * Can be stylist, beautician, therapist, etc.
     */
    private String assignedStaff;
    
    /**
     * Flag indicating if this is customer's first visit to salon
     */
    @Builder.Default
    private Boolean isFirstTime = false;
    
    /**
     * Confirmation code for appointment verification
     * Can be used for check-in or verification purposes
     */
    private String confirmationCode;
    
    // ==================== NOTIFICATION TRACKING ====================
    
    /**
     * Whether confirmation SMS/email has been sent to customer
     */
    @Builder.Default
    private Boolean confirmationSent = false;
    
    /**
     * Whether reminder has been sent (typically 24h before appointment)
     */
    @Builder.Default
    private Boolean reminderSent = false;
    
    /**
     * Timestamp when reminder was sent
     */
    private LocalDateTime reminderSentAt;
    
    // ==================== METADATA & AUDIT ====================
    
    /**
     * Timestamp when appointment was created
     * Auto-set on creation by Spring Data MongoDB
     */
    @CreatedDate
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when appointment was last modified
     * Auto-updated on modification by Spring Data MongoDB
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    /**
     * User ID of person who created the appointment
     * Can be customer ID, admin ID, or staff ID
     */
    private String createdBy;
    
    /**
     * User ID of person who last modified the appointment
     */
    private String lastModifiedBy;
    
    // ==================== OPTIONAL ADVANCED FEATURES ====================
    
    /**
     * Whether this is a recurring appointment
     */
    @Builder.Default
    private Boolean isRecurring = false;
    
    /**
     * Recurring pattern if applicable
     * Values: "WEEKLY", "BIWEEKLY", "MONTHLY", "CUSTOM"
     */
    private String recurringPattern;
    
    /**
     * Reference to parent recurring appointment (if this is a recurrence)
     */
    private String parentAppointmentId;
    
    /**
     * Whether this is a group booking (multiple customers)
     */
    @Builder.Default
    private Boolean isGroupBooking = false;
    
    /**
     * Additional customer IDs for group bookings
     */
    private java.util.List<String> additionalCustomerIds;
    
    /**
     * Whether customer has left a review for this appointment
     */
    @Builder.Default
    private Boolean hasReview = false;
    
    /**
     * Reference to review document ID (if review exists)
     */
    private String reviewId;
    
    // ==================== BUSINESS LOGIC METHODS ====================
    
    /**
     * Check if appointment can be cancelled
     * Only PENDING and CONFIRMED appointments can be cancelled
     * 
     * @return true if appointment can be cancelled
     */
    public boolean isCancellable() {
        return status != null && status.isCancellable();
    }
    
    /**
     * Check if appointment can be rescheduled
     * Only PENDING and CONFIRMED appointments can be rescheduled
     * 
     * @return true if appointment can be rescheduled
     */
    public boolean isReschedulable() {
        return status == AppointmentStatus.PENDING || status == AppointmentStatus.CONFIRMED;
    }
    
    /**
     * Check if appointment is in a final state (cannot be modified)
     * 
     * @return true if status is COMPLETED, CANCELLED, or NO_SHOW
     */
    public boolean isFinalState() {
        return status != null && status.isFinalState();
    }
    
    /**
     * Check if appointment can be confirmed
     * Only PENDING appointments can be confirmed
     * 
     * @return true if appointment is pending and can be confirmed
     */
    public boolean isConfirmable() {
        return status == AppointmentStatus.PENDING;
    }
    
    /**
     * Check if appointment can be marked as completed
     * Only CONFIRMED appointments can be completed
     * 
     * @return true if appointment is confirmed and can be completed
     */
    public boolean isCompletable() {
        return status == AppointmentStatus.CONFIRMED;
    }
    
    /**
     * Check if appointment can be marked as no-show
     * Only CONFIRMED appointments can be marked as no-show
     * 
     * @return true if appointment can be marked as no-show
     */
    public boolean canMarkAsNoShow() {
        return status == AppointmentStatus.CONFIRMED;
    }
    
    /**
     * Check if payment is pending
     * 
     * @return true if payment status is PENDING
     */
    public boolean isPaymentPending() {
        return paymentStatus == PaymentStatus.PENDING;
    }
    
    /**
     * Check if appointment is upcoming (confirmed and in future)
     * 
     * @return true if appointment is confirmed and scheduled for future
     */
    public boolean isUpcoming() {
        return status == AppointmentStatus.CONFIRMED && 
               timeSlot != null && 
               !timeSlot.isPast();
    }
}
