package com.example.salon_booking.service;

import com.example.salon_booking.dto.AppointmentRequestDTO;
import com.example.salon_booking.dto.AppointmentResponseDTO;
import com.example.salon_booking.dto.CustomerDTO;
import com.example.salon_booking.exception.DoubleBookingException;
import com.example.salon_booking.exception.InvalidAppointmentException;
import com.example.salon_booking.exception.ResourceNotFoundException;
import com.example.salon_booking.exception.TimeSlotNotAvailableException;
import com.example.salon_booking.models.*;
import com.example.salon_booking.repositories.AppointmentRepository;
import com.example.salon_booking.repositories.SalonRepository;
import com.example.salon_booking.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing appointment operations
 * Handles appointment booking, cancellation, and rescheduling with transaction management
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final CustomerService customerService;
    private final TimeSlotService timeSlotService;
    private final SalonRepository salonRepository;
    private final ServiceRepository serviceRepository;
    
    /**
     * Create a new appointment
     * This method handles the entire booking flow with transaction management
     * 
     * @param request Appointment request data
     * @return Created appointment
     * @throws TimeSlotNotAvailableException if time slot is not available
     * @throws DoubleBookingException if time slot is already booked
     */
    @Transactional
    public Appointment createAppointment(AppointmentRequestDTO request) {
        log.info("Creating appointment for customer: {} at salon: {}", 
                request.getCustomerEmail(), request.getSalonId());
        
        // 1. Verify time slot is available
        timeSlotService.verifySlotAvailability(request.getTimeSlotId());
        
        // 2. Check for double booking
        Boolean isBooked = appointmentRepository.existsByTimeSlotIdAndStatusNot(
                request.getTimeSlotId(), AppointmentStatus.CANCELLED);
        if (isBooked) {
            throw new DoubleBookingException("This time slot is already booked");
        }
        
        // 3. Get or create customer
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name(request.getCustomerName())
                .email(request.getCustomerEmail())
                .phone(request.getCustomerPhone())
                .preferredContact(request.getPreferredContact())
                .build();
        Customer customer = customerService.createOrGetCustomer(customerDTO);
        
        // 4. Fetch related entities
        TimeSlot timeSlot = timeSlotService.getTimeSlotById(request.getTimeSlotId());
        Salon salon = salonRepository.findById(request.getSalonId())
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id: " + request.getSalonId()));
        com.example.salon_booking.models.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + request.getServiceId()));
        
        // 5. Generate confirmation code
        String confirmationCode = generateConfirmationCode();
        
        // 6. Create appointment
        Appointment appointment = Appointment.builder()
                .customer(customer)
                .service(service)
                .timeSlot(timeSlot)
                .salon(salon)
                .bookingDate(LocalDateTime.now())
                .status(AppointmentStatus.PENDING)
                .customerNotes(request.getNotes())
                .confirmationCode(confirmationCode)
                .build();
        
        // 7. Save appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // 8. Mark time slot as unavailable
        timeSlotService.markSlotAsUnavailable(request.getTimeSlotId());
        
        log.info("Created appointment with ID: {} and confirmation code: {}", 
                savedAppointment.getId(), confirmationCode);
        
        return savedAppointment;
    }
    
    /**
     * Cancel an appointment
     * Frees up the time slot for other bookings
     * 
     * @param appointmentId Appointment ID
     * @param reason Cancellation reason
     * @return Updated appointment
     * @throws InvalidAppointmentException if appointment cannot be cancelled
     */
    @Transactional
    public Appointment cancelAppointment(String appointmentId, String reason) {
        log.info("Cancelling appointment: {}", appointmentId);
        
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Check if appointment can be cancelled
        if (!appointment.isCancellable()) {
            throw new InvalidAppointmentException(
                    "Appointment with status " + appointment.getStatus() + " cannot be cancelled");
        }
        
        // Update appointment status
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledAt(LocalDateTime.now());
        
        Appointment updated = appointmentRepository.save(appointment);
        
        // Free up the time slot
        timeSlotService.markSlotAsAvailable(appointment.getTimeSlot().getId());
        
        log.info("Cancelled appointment: {}", appointmentId);
        return updated;
    }
    
    /**
     * Reschedule an appointment to a new time slot
     * 
     * @param appointmentId Appointment ID
     * @param newTimeSlotId New time slot ID
     * @return Updated appointment
     * @throws InvalidAppointmentException if appointment cannot be rescheduled
     * @throws TimeSlotNotAvailableException if new time slot is not available
     */
    @Transactional
    public Appointment rescheduleAppointment(String appointmentId, String newTimeSlotId) {
        log.info("Rescheduling appointment {} to time slot {}", appointmentId, newTimeSlotId);
        
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Check if appointment can be rescheduled
        if (!appointment.isReschedulable()) {
            throw new InvalidAppointmentException(
                    "Appointment with status " + appointment.getStatus() + " cannot be rescheduled");
        }
        
        // Verify new time slot is available
        timeSlotService.verifySlotAvailability(newTimeSlotId);
        
        // Check for double booking on new slot
        Boolean isBooked = appointmentRepository.existsByTimeSlotIdAndStatusNot(
                newTimeSlotId, AppointmentStatus.CANCELLED);
        if (isBooked) {
            throw new DoubleBookingException("The new time slot is already booked");
        }
        
        // Free up old time slot
        String oldTimeSlotId = appointment.getTimeSlot().getId();
        timeSlotService.markSlotAsAvailable(oldTimeSlotId);
        
        // Get new time slot
        TimeSlot newTimeSlot = timeSlotService.getTimeSlotById(newTimeSlotId);
        
        // Update appointment
        appointment.setTimeSlot(newTimeSlot);
        // updatedAt is auto-updated by @LastModifiedDate
        
        Appointment updated = appointmentRepository.save(appointment);
        
        // Mark new time slot as unavailable
        timeSlotService.markSlotAsUnavailable(newTimeSlotId);
        
        log.info("Rescheduled appointment: {}", appointmentId);
        return updated;
    }
    
    /**
     * Confirm an appointment (change status to CONFIRMED)
     * 
     * @param appointmentId Appointment ID
     * @return Updated appointment
     */
    @Transactional
    public Appointment confirmAppointment(String appointmentId) {
        log.info("Confirming appointment: {}", appointmentId);
        
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmedAt(LocalDateTime.now());
        // updatedAt is auto-updated by @LastModifiedDate
        
        return appointmentRepository.save(appointment);
    }
    
    /**
     * Complete an appointment (change status to COMPLETED)
     * 
     * @param appointmentId Appointment ID
     * @return Updated appointment
     */
    @Transactional
    public Appointment completeAppointment(String appointmentId) {
        log.info("Completing appointment: {}", appointmentId);
        
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setCompletedAt(LocalDateTime.now());
        // updatedAt is auto-updated by @LastModifiedDate
        
        return appointmentRepository.save(appointment);
    }
    
    /**
     * Get appointment by ID
     * 
     * @param id Appointment ID
     * @return Appointment entity
     * @throws ResourceNotFoundException if appointment not found
     */
    public Appointment getAppointmentById(String id) {
        log.info("Fetching appointment with ID: {}", id);
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }
    
    /**
     * Get appointment by confirmation code
     * 
     * @param confirmationCode Confirmation code
     * @return Appointment entity
     * @throws ResourceNotFoundException if appointment not found
     */
    public Appointment getAppointmentByConfirmationCode(String confirmationCode) {
        log.info("Fetching appointment with confirmation code: {}", confirmationCode);
        return appointmentRepository.findByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with confirmation code: " + confirmationCode));
    }
    
    /**
     * Get all appointments for a customer
     * 
     * @param customerId Customer ID
     * @return List of appointments
     */
    public List<Appointment> getCustomerAppointments(String customerId) {
        log.info("Fetching appointments for customer: {}", customerId);
        return appointmentRepository.findByCustomerIdOrderByBookingDateDesc(customerId);
    }
    
    /**
     * Get all appointments for a salon
     * 
     * @param salonId Salon ID
     * @return List of appointments
     */
    public List<Appointment> getSalonAppointments(String salonId) {
        log.info("Fetching appointments for salon: {}", salonId);
        return appointmentRepository.findBySalonId(salonId);
    }
    
    /**
     * Get appointments by status
     * 
     * @param status Appointment status
     * @return List of appointments
     */
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        log.info("Fetching appointments with status: {}", status);
        return appointmentRepository.findByStatus(status);
    }
    
    /**
     * Get all appointments
     * 
     * @return List of all appointments
     */
    public List<Appointment> getAllAppointments() {
        log.info("Fetching all appointments");
        return appointmentRepository.findAll();
    }
    
    /**
     * Convert Appointment entity to response DTO
     * 
     * @param appointment Appointment entity
     * @return AppointmentResponseDTO
     */
    public AppointmentResponseDTO convertToResponseDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .confirmationCode(appointment.getConfirmationCode())
                .customerId(appointment.getCustomer().getId())
                .customerName(appointment.getCustomer().getName())
                .customerEmail(appointment.getCustomer().getEmail())
                .customerPhone(appointment.getCustomer().getPhone())
                .serviceId(appointment.getService().getId())
                .serviceName(appointment.getService().getName())
                .timeSlotId(appointment.getTimeSlot().getId())
                .appointmentDate(appointment.getTimeSlot().getDate())
                .startTime(appointment.getTimeSlot().getStartTime())
                .endTime(appointment.getTimeSlot().getEndTime())
                .salonId(appointment.getSalon().getId())
                .salonName(appointment.getSalon().getName())
                .salonAddress(appointment.getSalon().getAddress())
                .salonPhone(appointment.getSalon().getPhone())
                .status(appointment.getStatus())
                .bookingDate(appointment.getBookingDate())
                .lastModifiedDate(appointment.getUpdatedAt())
                .notes(appointment.getCustomerNotes())
                .assignedStaff(appointment.getAssignedStaff())
                .cancellationReason(appointment.getCancellationReason())
                .build();
    }
    
    /**
     * Convert list of appointments to response DTOs
     * 
     * @param appointments List of appointments
     * @return List of AppointmentResponseDTO
     */
    public List<AppointmentResponseDTO> convertToResponseDTOs(List<Appointment> appointments) {
        return appointments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Generate a unique confirmation code
     * 
     * @return Confirmation code
     */
    private String generateConfirmationCode() {
        return "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
