package com.example.salon_booking.controllers;

import com.example.salon_booking.dto.AppointmentRequestDTO;
import com.example.salon_booking.dto.AppointmentResponseDTO;
import com.example.salon_booking.models.Appointment;
import com.example.salon_booking.models.AppointmentStatus;
import com.example.salon_booking.models.TimeSlot;
import com.example.salon_booking.service.AppointmentService;
import com.example.salon_booking.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Appointment operations
 * Handles all appointment-related HTTP requests
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    private final TimeSlotService timeSlotService;
    
    /**
     * Create a new appointment
     * POST /api/appointments
     * 
     * @param request Appointment request data
     * @return Created appointment with confirmation code
     */
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentRequestDTO request) {
        log.info("Received appointment request for customer: {}", request.getCustomerEmail());
        
        Appointment appointment = appointmentService.createAppointment(request);
        AppointmentResponseDTO response = appointmentService.convertToResponseDTO(appointment);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get appointment by ID
     * GET /api/appointments/{id}
     * 
     * @param id Appointment ID
     * @return Appointment details
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(@PathVariable String id) {
        log.info("Fetching appointment: {}", id);
        
        Appointment appointment = appointmentService.getAppointmentById(id);
        AppointmentResponseDTO response = appointmentService.convertToResponseDTO(appointment);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get appointment by confirmation code
     * GET /api/appointments/confirmation/{code}
     * 
     * @param code Confirmation code
     * @return Appointment details
     */
    @GetMapping("/confirmation/{code}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentByConfirmationCode(
            @PathVariable String code) {
        log.info("Fetching appointment with confirmation code: {}", code);
        
        Appointment appointment = appointmentService.getAppointmentByConfirmationCode(code);
        AppointmentResponseDTO response = appointmentService.convertToResponseDTO(appointment);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all appointments for a customer
     * GET /api/appointments/customer/{customerId}
     * 
     * @param customerId Customer ID
     * @return List of customer's appointments
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getCustomerAppointments(
            @PathVariable String customerId) {
        log.info("Fetching appointments for customer: {}", customerId);
        
        List<Appointment> appointments = appointmentService.getCustomerAppointments(customerId);
        List<AppointmentResponseDTO> response = appointmentService.convertToResponseDTOs(appointments);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all appointments for a salon
     * GET /api/appointments/salon/{salonId}
     * 
     * @param salonId Salon ID
     * @return List of salon's appointments
     */
    @GetMapping("/salon/{salonId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getSalonAppointments(
            @PathVariable String salonId) {
        log.info("Fetching appointments for salon: {}", salonId);
        
        List<Appointment> appointments = appointmentService.getSalonAppointments(salonId);
        List<AppointmentResponseDTO> response = appointmentService.convertToResponseDTOs(appointments);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get appointments by status
     * GET /api/appointments/status/{status}
     * 
     * @param status Appointment status (PENDING, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW)
     * @return List of appointments with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByStatus(
            @PathVariable String status) {
        log.info("Fetching appointments with status: {}", status);
        
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(appointmentStatus);
        List<AppointmentResponseDTO> response = appointmentService.convertToResponseDTOs(appointments);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all appointments (admin)
     * GET /api/appointments
     * 
     * @return List of all appointments
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        log.info("Fetching all appointments");
        
        List<Appointment> appointments = appointmentService.getAllAppointments();
        List<AppointmentResponseDTO> response = appointmentService.convertToResponseDTOs(appointments);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cancel an appointment
     * DELETE /api/appointments/{id}
     * 
     * @param id Appointment ID
     * @param reason Cancellation reason (optional)
     * @return Updated appointment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        log.info("Cancelling appointment: {}", id);
        
        Appointment appointment = appointmentService.cancelAppointment(id, reason);
        AppointmentResponseDTO response = appointmentService.convertToResponseDTO(appointment);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reschedule an appointment
     * PUT /api/appointments/{id}/reschedule
     * 
     * @param id Appointment ID
     * @param newTimeSlotId New time slot ID
     * @return Updated appointment
     */
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponseDTO> rescheduleAppointment(
            @PathVariable String id,
            @RequestParam String newTimeSlotId) {
        log.info("Rescheduling appointment {} to time slot {}", id, newTimeSlotId);
        
        Appointment appointment = appointmentService.rescheduleAppointment(id, newTimeSlotId);
        AppointmentResponseDTO response = appointmentService.convertToResponseDTO(appointment);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Confirm an appointment
     * PUT /api/appointments/{id}/confirm
     * 
     * @param id Appointment ID
     * @return Updated appointment
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponseDTO> confirmAppointment(@PathVariable String id) {
        log.info("Confirming appointment: {}", id);
        
        Appointment appointment = appointmentService.confirmAppointment(id);
        AppointmentResponseDTO response = appointmentService.convertToResponseDTO(appointment);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Complete an appointment
     * PUT /api/appointments/{id}/complete
     * 
     * @param id Appointment ID
     * @return Updated appointment
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(@PathVariable String id) {
        log.info("Completing appointment: {}", id);
        
        Appointment appointment = appointmentService.completeAppointment(id);
        AppointmentResponseDTO response = appointmentService.convertToResponseDTO(appointment);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get available time slots for a salon on a specific date
     * GET /api/appointments/slots/available
     * 
     * @param salonId Salon ID
     * @param date Date in format yyyy-MM-dd
     * @return List of available time slots
     */
    @GetMapping("/slots/available")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(
            @RequestParam String salonId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Fetching available slots for salon {} on {}", salonId, date);
        
        List<TimeSlot> availableSlots = timeSlotService.getAvailableSlots(salonId, date);
        
        return ResponseEntity.ok(availableSlots);
    }
    
    /**
     * Generate time slots for a salon for the next week
     * POST /api/appointments/slots/generate
     * 
     * @param salonId Salon ID
     * @param startDate Start date (optional, defaults to today)
     * @return Success message
     */
    @PostMapping("/slots/generate")
    public ResponseEntity<Map<String, String>> generateTimeSlots(
            @RequestParam String salonId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        log.info("Generating time slots for salon: {}", salonId);
        
        LocalDate start = startDate != null ? startDate : LocalDate.now();
        timeSlotService.generateSlotsForWeek(salonId, start);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Time slots generated successfully for salon " + salonId);
        response.put("startDate", start.toString());
        
        return ResponseEntity.ok(response);
    }
}
