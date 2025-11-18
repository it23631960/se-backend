# Appointment Booking System Documentation

## Overview

This document provides comprehensive documentation for the complete Salon Appointment Booking System that has been added to the salon-booking backend.

## Table of Contents

1. [Architecture](#architecture)
2. [Models](#models)
3. [Repositories](#repositories)
4. [DTOs](#dtos)
5. [Services](#services)
6. [Controllers](#controllers)
7. [Exception Handling](#exception-handling)
8. [Data Initialization](#data-initialization)
9. [API Endpoints](#api-endpoints)
10. [Business Logic](#business-logic)
11. [Testing](#testing)

---

## Architecture

The appointment booking system follows a layered architecture:

```
┌─────────────────────────────────────────────────┐
│              REST Controllers                    │
│  (AppointmentController, handle HTTP requests)  │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│              Service Layer                       │
│  (Business logic, transaction management)       │
│  - AppointmentService                           │
│  - CustomerService                              │
│  - TimeSlotService                              │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│         Repository Layer                         │
│  (Data access with Spring Data MongoDB)         │
│  - AppointmentRepository                        │
│  - CustomerRepository                           │
│  - TimeSlotRepository                           │
│  - ServiceRepository                            │
│  - SalonRepository                              │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│            MongoDB Database                      │
│  (salon-booking database on MongoDB Atlas)      │
└─────────────────────────────────────────────────┘
```

---

## Models

### 1. Customer
**Location:** `models/Customer.java`

Represents a customer who books appointments.

**Fields:**
- `id` (String): Unique identifier
- `name` (String, required): Customer's full name
- `email` (String, required, unique): Customer's email
- `phone` (String, required): Customer's phone number (validated format)
- `preferredContact` (String): Preferred contact method (EMAIL, PHONE, SMS)
- `createdDate` (LocalDateTime): Account creation timestamp
- `lastModifiedDate` (LocalDateTime): Last update timestamp

**Validations:**
- Email format validation with `@Email`
- Phone format validation: `^\+?[0-9]{10,15}$`
- Unique email index in MongoDB

**Indexes:**
- `@Indexed(unique = true)` on email field

---

### 2. TimeSlot
**Location:** `models/TimeSlot.java`

Represents available booking time slots for salons.

**Fields:**
- `id` (String): Unique identifier
- `date` (LocalDate, required): Date of the slot
- `startTime` (LocalTime, required): Start time
- `endTime` (LocalTime, required): End time
- `isAvailable` (Boolean): Availability status
- `salon` (Salon, DBRef): Reference to the salon
- `createdDate` (LocalDateTime): Creation timestamp

**Helper Methods:**
- `isPast()`: Check if slot is in the past
- `getDurationMinutes()`: Calculate slot duration

**Compound Index:**
```java
@CompoundIndex(name = "salon_date_time_idx", 
               def = "{'date': 1, 'startTime': 1, 'salon.$id': 1}", 
               unique = true)
```
Ensures no duplicate time slots for same salon at same date/time.

---

### 3. Appointment
**Location:** `models/Appointment.java`

Represents a booked appointment linking customer, service, time slot, and salon.

**Fields:**
- `id` (String): Unique identifier
- `customer` (Customer, DBRef, required): Customer reference
- `service` (Service, DBRef, required): Service being booked
- `timeSlot` (TimeSlot, DBRef, required): Time slot reference
- `salon` (Salon, DBRef, required): Salon reference
- `bookingDate` (LocalDateTime): When booking was made
- `lastModifiedDate` (LocalDateTime): Last update timestamp
- `status` (AppointmentStatus, required): Current status
- `confirmationCode` (String, unique): Unique confirmation code
- `notes` (String): Additional notes
- `assignedStaff` (String): Staff member assigned
- `cancellationReason` (String): Reason if cancelled

**Helper Methods:**
- `isCancellable()`: Check if appointment can be cancelled
- `isReschedulable()`: Check if appointment can be rescheduled

**Status Transitions:**
```
PENDING → CONFIRMED → COMPLETED
   ↓          ↓
CANCELLED  CANCELLED
   ↓
NO_SHOW (if not completed)
```

---

### 4. AppointmentStatus (Enum)
**Location:** `models/AppointmentStatus.java`

**Values:**
- `PENDING`: Initial status when appointment is created
- `CONFIRMED`: Customer or salon confirmed the appointment
- `COMPLETED`: Service was completed
- `CANCELLED`: Appointment was cancelled
- `NO_SHOW`: Customer didn't show up

---

### 5. Service
**Location:** `models/Service.java`

Represents salon services that can be booked.

**Fields:**
- `id` (String): Unique identifier
- `name` (String, required): Service name
- `description` (String): Service description
- `price` (double, min=0): Service price
- `durationMinutes` (int, min=0): Service duration
- `category` (String): Service category (Hair, Nails, Spa, Makeup, Waxing)
- `active` (boolean): Whether service is currently offered

---

## Repositories

### 1. CustomerRepository
**Location:** `repositories/CustomerRepository.java`

**Custom Query Methods:**
```java
Optional<Customer> findByEmail(String email)
Boolean existsByEmail(String email)
Optional<Customer> findByPhone(String phone)
List<Customer> findByNameContainingIgnoreCase(String name)
```

---

### 2. TimeSlotRepository
**Location:** `repositories/TimeSlotRepository.java`

**Custom Query Methods:**
```java
List<TimeSlot> findBySalonIdAndDate(String salonId, LocalDate date)
List<TimeSlot> findBySalonIdAndDateAndIsAvailableTrue(String salonId, LocalDate date)
List<TimeSlot> findBySalonIdAndDateBetween(String salonId, LocalDate startDate, LocalDate endDate)
Boolean existsBySalonIdAndDateAndStartTimeAndEndTime(String salonId, LocalDate date, 
                                                      LocalTime startTime, LocalTime endTime)
```

---

### 3. AppointmentRepository
**Location:** `repositories/AppointmentRepository.java`

**Custom Query Methods:**
```java
List<Appointment> findByCustomerIdOrderByBookingDateDesc(String customerId)
List<Appointment> findBySalonId(String salonId)
List<Appointment> findByStatus(AppointmentStatus status)
Optional<Appointment> findByConfirmationCode(String confirmationCode)
Boolean existsByTimeSlotIdAndStatusNot(String timeSlotId, AppointmentStatus status)
List<Appointment> findByCustomerIdAndStatus(String customerId, AppointmentStatus status)
List<Appointment> findBySalonIdAndStatus(String salonId, AppointmentStatus status)
```

---

### 4. ServiceRepository
**Location:** `repositories/ServiceRepository.java`

**Custom Query Methods:**
```java
List<Service> findByActiveTrue()
List<Service> findByCategory(String category)
List<Service> findByCategoryAndActiveTrue(String category)
List<Service> findByPriceBetween(double minPrice, double maxPrice)
List<Service> findByPriceBetweenAndActiveTrue(double minPrice, double maxPrice)
List<Service> findByNameContainingIgnoreCase(String name)
```

---

## DTOs

### 1. AppointmentRequestDTO
**Location:** `dto/AppointmentRequestDTO.java`

Used for creating appointment requests from the frontend.

**Fields:**
```java
@NotBlank String customerName
@Email String customerEmail
@Pattern(regexp = "^\\+?[0-9]{10,15}$") String customerPhone
String preferredContact
@NotBlank String salonId
@NotBlank String serviceId
@NotBlank String timeSlotId
String notes
```

---

### 2. AppointmentResponseDTO
**Location:** `dto/AppointmentResponseDTO.java`

Used for returning appointment details to the frontend.

**Fields:**
- All appointment details
- Flattened customer information (name, email, phone)
- Flattened service information (name)
- Flattened time slot information (date, start/end time)
- Flattened salon information (name, address, phone)

---

### 3. CustomerDTO
**Location:** `dto/CustomerDTO.java`

Used for customer data transfer.

---

### 4. TimeSlotDTO
**Location:** `dto/TimeSlotDTO.java`

Used for time slot data transfer.

---

## Services

### 1. AppointmentService
**Location:** `service/AppointmentService.java`

**Core Methods:**

#### `createAppointment(AppointmentRequestDTO request)`
Creates a new appointment with transaction management.

**Flow:**
1. Verify time slot is available
2. Check for double booking
3. Get or create customer
4. Fetch related entities (salon, service, time slot)
5. Generate unique confirmation code (format: `APT-XXXXXXXX`)
6. Create appointment with PENDING status
7. Save appointment
8. Mark time slot as unavailable

**Exceptions:**
- `TimeSlotNotAvailableException`: Slot not available
- `DoubleBookingException`: Slot already booked
- `ResourceNotFoundException`: Salon or service not found

---

#### `cancelAppointment(String appointmentId, String reason)`
Cancels an appointment and frees the time slot.

**Flow:**
1. Fetch appointment
2. Verify appointment is cancellable (not COMPLETED or already CANCELLED)
3. Update status to CANCELLED
4. Store cancellation reason
5. Free up the time slot

**Exceptions:**
- `InvalidAppointmentException`: Cannot cancel completed/cancelled appointments
- `ResourceNotFoundException`: Appointment not found

---

#### `rescheduleAppointment(String appointmentId, String newTimeSlotId)`
Reschedules an appointment to a new time slot.

**Flow:**
1. Fetch appointment
2. Verify appointment is reschedulable
3. Verify new time slot is available
4. Check for double booking on new slot
5. Free old time slot
6. Update appointment with new time slot
7. Mark new time slot as unavailable

**Exceptions:**
- `InvalidAppointmentException`: Cannot reschedule completed/cancelled appointments
- `TimeSlotNotAvailableException`: New slot not available
- `DoubleBookingException`: New slot already booked

---

#### Other Methods:
- `confirmAppointment(String appointmentId)`: Change status to CONFIRMED
- `completeAppointment(String appointmentId)`: Change status to COMPLETED
- `getAppointmentById(String id)`: Retrieve by ID
- `getAppointmentByConfirmationCode(String code)`: Retrieve by confirmation code
- `getCustomerAppointments(String customerId)`: Get all customer appointments
- `getSalonAppointments(String salonId)`: Get all salon appointments
- `getAppointmentsByStatus(AppointmentStatus status)`: Filter by status
- `convertToResponseDTO(Appointment)`: Convert to DTO

---

### 2. CustomerService
**Location:** `service/CustomerService.java`

**Core Methods:**

#### `createOrGetCustomer(CustomerDTO customerDTO)`
Creates a new customer or returns existing one by email.

**Flow:**
1. Check if customer exists by email
2. If exists, return existing customer
3. If not, create new customer with validated data
4. Save and return customer

---

#### Other Methods:
- `getCustomerById(String id)`: Retrieve by ID
- `getCustomerByEmail(String email)`: Retrieve by email
- `getAllCustomers()`: Get all customers
- `searchCustomersByName(String name)`: Search by name
- `updateCustomer(String id, CustomerDTO dto)`: Update customer
- `deleteCustomer(String id)`: Delete customer

---

### 3. TimeSlotService
**Location:** `service/TimeSlotService.java`

**Core Methods:**

#### `getAvailableSlots(String salonId, LocalDate date)`
Returns available time slots for a salon on a specific date.

**Flow:**
1. Fetch all slots for salon and date where `isAvailable=true`
2. If date is today, filter out past slots
3. Return filtered list

---

#### `generateSlotsForWeek(String salonId, LocalDate startDate)`
Generates time slots for 7 days starting from a date.

**Configuration:**
- Slot duration: 30 minutes
- Default hours: 9:00 AM - 6:00 PM
- Uses salon's `openTime` and `closeTime` if available

**Flow:**
1. Fetch salon details
2. For each of next 7 days:
   - Skip past dates
   - Check if slots already exist
   - Parse salon open/close times or use defaults
   - Generate 30-minute slots
   - Save all slots in batch

---

#### `generateSlotsForAllSalons(LocalDate startDate)`
Generates slots for all salons in the database.

**Flow:**
1. Fetch all salons
2. For each salon, call `generateSlotsForWeek()`
3. Log any errors but continue processing other salons

---

#### Other Methods:
- `getTimeSlotById(String id)`: Retrieve slot by ID
- `verifySlotAvailability(String id)`: Check if slot is available (throws exception if not)
- `markSlotAsUnavailable(String id)`: Mark slot as unavailable
- `markSlotAsAvailable(String id)`: Mark slot as available

---

## Controllers

### AppointmentController
**Location:** `controllers/AppointmentController.java`

**Base Path:** `/api/appointments`

**Endpoints:** See [API Endpoints](#api-endpoints) section below.

---

## Exception Handling

### Custom Exceptions

1. **TimeSlotNotAvailableException**
   - HTTP Status: 409 Conflict
   - Use case: Time slot is not available for booking

2. **DoubleBookingException**
   - HTTP Status: 409 Conflict
   - Use case: Attempting to book an already booked slot

3. **CustomerNotFoundException**
   - HTTP Status: 404 Not Found
   - Use case: Customer not found by ID or email

4. **InvalidAppointmentException**
   - HTTP Status: 400 Bad Request
   - Use case: Invalid appointment operation (e.g., cancelling completed appointment)

5. **ResourceNotFoundException**
   - HTTP Status: 404 Not Found
   - Use case: Generic resource not found

### GlobalExceptionHandler
**Location:** `exception/GlobalExceptionHandler.java`

Handles all exceptions and returns consistent error responses:

```json
{
  "message": "Error message",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## Data Initialization

### DataInitializer
**Location:** `config/DataInitializer.java`

Runs on application startup using `CommandLineRunner`.

#### What it does:

1. **Seeds Services:**
   - Only runs if no services exist
   - Creates 24 predefined services across categories:
     - Hair (6 services): Haircut, Coloring, Highlights, etc.
     - Nails (5 services): Manicure, Pedicure, Gel, Acrylic, Nail Art
     - Spa (5 services): Facials, Massages, Body treatments
     - Makeup (4 services): Makeup application, Bridal, Eyebrows, Lashes
     - Waxing (4 services): Leg, Arm, Facial, Bikini

2. **Generates Time Slots:**
   - Generates slots for all salons for next 7 days
   - Uses `TimeSlotService.generateSlotsForAllSalons()`
   - Skips if slots already exist

---

## API Endpoints

### Appointment Endpoints

#### 1. Create Appointment
```http
POST /api/appointments
Content-Type: application/json

{
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "+1234567890",
  "preferredContact": "EMAIL",
  "salonId": "salon123",
  "serviceId": "service456",
  "timeSlotId": "slot789",
  "notes": "First time customer"
}

Response: 201 Created
{
  "id": "apt123",
  "confirmationCode": "APT-ABCD1234",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "serviceName": "Haircut - Men",
  "appointmentDate": "2024-01-20",
  "startTime": "10:00:00",
  "endTime": "10:30:00",
  "salonName": "Elite Salon",
  "status": "PENDING",
  ...
}
```

---

#### 2. Get Appointment by ID
```http
GET /api/appointments/{id}

Response: 200 OK
```

---

#### 3. Get Appointment by Confirmation Code
```http
GET /api/appointments/confirmation/APT-ABCD1234

Response: 200 OK
```

---

#### 4. Get Customer Appointments
```http
GET /api/appointments/customer/{customerId}

Response: 200 OK
[
  { appointment 1 },
  { appointment 2 }
]
```

---

#### 5. Get Salon Appointments
```http
GET /api/appointments/salon/{salonId}

Response: 200 OK
```

---

#### 6. Get Appointments by Status
```http
GET /api/appointments/status/PENDING

Response: 200 OK
```

---

#### 7. Get All Appointments
```http
GET /api/appointments

Response: 200 OK
```

---

#### 8. Cancel Appointment
```http
DELETE /api/appointments/{id}?reason=Personal%20reasons

Response: 200 OK
```

---

#### 9. Reschedule Appointment
```http
PUT /api/appointments/{id}/reschedule?newTimeSlotId=newSlot456

Response: 200 OK
```

---

#### 10. Confirm Appointment
```http
PUT /api/appointments/{id}/confirm

Response: 200 OK
```

---

#### 11. Complete Appointment
```http
PUT /api/appointments/{id}/complete

Response: 200 OK
```

---

### Time Slot Endpoints

#### 12. Get Available Slots
```http
GET /api/appointments/slots/available?salonId=salon123&date=2024-01-20

Response: 200 OK
[
  {
    "id": "slot1",
    "date": "2024-01-20",
    "startTime": "09:00:00",
    "endTime": "09:30:00",
    "isAvailable": true
  },
  ...
]
```

---

#### 13. Generate Time Slots
```http
POST /api/appointments/slots/generate?salonId=salon123&startDate=2024-01-15

Response: 200 OK
{
  "message": "Time slots generated successfully for salon salon123",
  "startDate": "2024-01-15"
}
```

---

## Business Logic

### Double Booking Prevention

The system prevents double booking through:

1. **Database Constraint:** Compound unique index on TimeSlot (date + time + salon)
2. **Application Logic:** Check in `AppointmentService.createAppointment()`:
   ```java
   Boolean isBooked = appointmentRepository.existsByTimeSlotIdAndStatusNot(
       timeSlotId, AppointmentStatus.CANCELLED);
   if (isBooked) {
       throw new DoubleBookingException("This time slot is already booked");
   }
   ```
3. **Transaction Management:** `@Transactional` ensures atomic operations

---

### Time Slot Availability

**Slot Generation:**
- 30-minute intervals
- Default: 9:00 AM - 6:00 PM
- Respects salon's `openTime` and `closeTime`
- Automatically generated for 7 days on startup

**Availability Rules:**
- Past slots are filtered out in `getAvailableSlots()`
- Slots marked unavailable when appointment is created
- Slots marked available when appointment is cancelled

---

### Appointment Lifecycle

```
CREATE → PENDING
   ↓
CONFIRM → CONFIRMED
   ↓
COMPLETE → COMPLETED

At any PENDING or CONFIRMED:
   ↓
CANCEL → CANCELLED
```

**Validation:**
- Can only cancel PENDING or CONFIRMED appointments
- Can only reschedule PENDING or CONFIRMED appointments
- Cannot modify COMPLETED or CANCELLED appointments

---

### Customer Management

**Create or Get Pattern:**
- When booking, system checks if customer exists by email
- If exists, uses existing customer record
- If not, creates new customer
- Prevents duplicate customers with same email

---

### Confirmation Codes

- Format: `APT-XXXXXXXX` (8 random uppercase characters)
- Generated using UUID substring
- Allows customers to retrieve appointments without account

---

## Testing

### Manual Testing with PowerShell

#### 1. Create an Appointment
```powershell
$body = @{
    customerName = "John Doe"
    customerEmail = "john@example.com"
    customerPhone = "+1234567890"
    preferredContact = "EMAIL"
    salonId = "YOUR_SALON_ID"
    serviceId = "YOUR_SERVICE_ID"
    timeSlotId = "YOUR_TIMESLOT_ID"
    notes = "First visit"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/appointments" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

#### 2. Get Available Slots
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/available?salonId=SALON_ID&date=2024-01-20"
```

#### 3. Generate Slots
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/generate?salonId=SALON_ID" `
    -Method POST
```

#### 4. Cancel Appointment
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/APT_ID?reason=Personal" `
    -Method DELETE
```

---

### Testing Workflow

1. **Start Backend:**
   ```powershell
   cd backend\salon-booking
   .\mvnw.cmd spring-boot:run
   ```

2. **Verify Data Initialization:**
   - Check logs for "Seeded 24 services"
   - Check logs for "Time slot generation completed"

3. **Get a Salon ID:**
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8080/api/salons"
   ```

4. **Get Available Services:**
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8080/api/services"
   ```

5. **Get Available Slots:**
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/available?salonId=SALON_ID&date=2024-01-20"
   ```

6. **Create Appointment:**
   Use the PowerShell script above with real IDs

7. **Verify Appointment:**
   - Check appointment was created
   - Verify confirmation code returned
   - Verify time slot marked as unavailable

8. **Test Cancellation:**
   - Cancel the appointment
   - Verify time slot is available again

---

## Summary

The complete Salon Appointment Booking System includes:

✅ **4 New Models:** Customer, TimeSlot, Appointment, AppointmentStatus  
✅ **5 Repositories:** Customer, TimeSlot, Appointment, Service, enhanced SalonRepository  
✅ **4 DTOs:** AppointmentRequest, AppointmentResponse, Customer, TimeSlot  
✅ **3 Services:** AppointmentService, CustomerService, TimeSlotService  
✅ **1 Controller:** AppointmentController with 13 endpoints  
✅ **4 Custom Exceptions:** TimeSlotNotAvailable, DoubleBooking, CustomerNotFound, InvalidAppointment  
✅ **Enhanced Exception Handler:** GlobalExceptionHandler with new exception handlers  
✅ **Data Initializer:** Seeds 24 services and generates time slots  
✅ **Transaction Management:** Atomic operations with @Transactional  
✅ **Validation:** Jakarta Validation on all DTOs and models  
✅ **Logging:** Comprehensive logging with @Slf4j  
✅ **Documentation:** Complete API documentation with examples

The system is production-ready with proper error handling, transaction management, validation, and business logic for double-booking prevention and appointment lifecycle management.

---

## Next Steps

1. **Frontend Integration:**
   - Update `bookingApiService.ts` with new appointment endpoints
   - Create booking flow components
   - Add confirmation code lookup feature

2. **Additional Features (Future):**
   - Email/SMS notifications for booking confirmations
   - Reminder notifications before appointments
   - Staff assignment management
   - Recurring appointments
   - Waitlist functionality
   - Review system integration (link reviews to completed appointments)
   - Payment integration
   - Admin dashboard for appointment management

3. **Security Enhancements:**
   - Add JWT authentication
   - Implement role-based access control (CUSTOMER, STAFF, ADMIN)
   - Secure sensitive endpoints

4. **Performance Optimizations:**
   - Add caching for frequently accessed data
   - Implement pagination for large result sets
   - Add database indexes based on query patterns

---

**Document Version:** 1.0  
**Last Updated:** January 2024  
**Author:** Salon Booking System Development Team
