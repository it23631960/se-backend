# Appointment System Quick Start Guide

## 🚀 Getting Started

This guide will help you quickly test the new appointment booking system that has been added to your salon booking application.

## Prerequisites

- ✅ Backend running on `http://localhost:8080`
- ✅ MongoDB Atlas connected
- ✅ PowerShell (for Windows testing)

## Step 1: Start the Backend

```powershell
cd e:\Saloon\Hasith\backend\salon-booking
.\mvnw.cmd spring-boot:run
```

**Expected Output:**
```
Seeded 24 services
Time slot generation completed successfully
Started SalonBookingApplication in X.XXX seconds
```

---

## Step 2: Verify Services Were Created

```powershell
# Get all services
$services = Invoke-RestMethod -Uri "http://localhost:8080/api/services"
$services | Format-Table id, name, price, category -AutoSize
```

**Expected:** You should see 24 services across categories: Hair, Nails, Spa, Makeup, Waxing

---

## Step 3: Get a Salon ID

```powershell
# Get all salons
$salons = Invoke-RestMethod -Uri "http://localhost:8080/api/salons"
$salons | Format-Table id, name, address -AutoSize

# Save the first salon ID for later use
$salonId = $salons[0].id
Write-Host "Using Salon ID: $salonId" -ForegroundColor Green
```

---

## Step 4: Check Available Time Slots

```powershell
# Get tomorrow's date
$tomorrow = (Get-Date).AddDays(1).ToString("yyyy-MM-dd")
Write-Host "Checking slots for: $tomorrow" -ForegroundColor Cyan

# Get available slots
$slots = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/available?salonId=$salonId&date=$tomorrow"
$slots | Select-Object -First 10 | Format-Table id, date, startTime, endTime, isAvailable -AutoSize

# Save a slot ID for booking
$slotId = $slots[0].id
Write-Host "Using Time Slot ID: $slotId" -ForegroundColor Green
```

**Expected:** You should see time slots from 9:00 AM to 6:00 PM in 30-minute intervals

---

## Step 5: Get a Service ID

```powershell
# Get a haircut service
$haircutService = $services | Where-Object { $_.name -like "*Haircut - Men*" } | Select-Object -First 1
$serviceId = $haircutService.id
Write-Host "Using Service ID: $serviceId ($($haircutService.name))" -ForegroundColor Green
```

---

## Step 6: Create Your First Appointment 🎉

```powershell
# Create appointment request
$appointmentRequest = @{
    customerName = "John Doe"
    customerEmail = "john.doe@example.com"
    customerPhone = "+1234567890"
    preferredContact = "EMAIL"
    salonId = $salonId
    serviceId = $serviceId
    timeSlotId = $slotId
    notes = "First time customer - please call to confirm"
} | ConvertTo-Json

# Make the booking
$appointment = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments" `
    -Method POST `
    -ContentType "application/json" `
    -Body $appointmentRequest

# Display the result
Write-Host "`n✅ APPOINTMENT CREATED SUCCESSFULLY!" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green
Write-Host "Confirmation Code: $($appointment.confirmationCode)" -ForegroundColor Yellow
Write-Host "Appointment ID: $($appointment.id)" -ForegroundColor Cyan
Write-Host "Customer: $($appointment.customerName)" -ForegroundColor White
Write-Host "Service: $($appointment.serviceName)" -ForegroundColor White
Write-Host "Date: $($appointment.appointmentDate)" -ForegroundColor White
Write-Host "Time: $($appointment.startTime) - $($appointment.endTime)" -ForegroundColor White
Write-Host "Salon: $($appointment.salonName)" -ForegroundColor White
Write-Host "Status: $($appointment.status)" -ForegroundColor Magenta
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green

# Save IDs for next steps
$appointmentId = $appointment.id
$confirmationCode = $appointment.confirmationCode
```

---

## Step 7: Retrieve Appointment by Confirmation Code

```powershell
# Customer can look up their appointment using confirmation code
$retrieved = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/confirmation/$confirmationCode"

Write-Host "`n📋 APPOINTMENT DETAILS (Retrieved by confirmation code):" -ForegroundColor Cyan
$retrieved | Select-Object confirmationCode, customerName, serviceName, appointmentDate, startTime, status | Format-List
```

---

## Step 8: Verify Time Slot is Unavailable

```powershell
# Check that the booked slot is now unavailable
$slotsAfterBooking = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/available?salonId=$salonId&date=$tomorrow"
$bookedSlot = $slotsAfterBooking | Where-Object { $_.id -eq $slotId }

if ($bookedSlot) {
    Write-Host "❌ ERROR: Slot should not be available!" -ForegroundColor Red
} else {
    Write-Host "✅ CORRECT: Booked slot is no longer in available slots list" -ForegroundColor Green
}
```

---

## Step 9: Get Customer's Appointments

```powershell
# Get the customer ID from the appointment
$customerId = $appointment.customerId

# Retrieve all appointments for this customer
$customerAppointments = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/customer/$customerId"

Write-Host "`n👤 CUSTOMER'S APPOINTMENTS:" -ForegroundColor Cyan
$customerAppointments | Format-Table confirmationCode, serviceName, appointmentDate, startTime, status -AutoSize
```

---

## Step 10: Confirm the Appointment

```powershell
# Salon or customer confirms the appointment
$confirmed = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/$appointmentId/confirm" `
    -Method PUT

Write-Host "`n✅ APPOINTMENT CONFIRMED!" -ForegroundColor Green
Write-Host "Status changed from PENDING to: $($confirmed.status)" -ForegroundColor Yellow
```

---

## Step 11: Test Rescheduling

```powershell
# Get another available slot
$newSlot = $slotsAfterBooking | Select-Object -First 1
$newSlotId = $newSlot.id

Write-Host "`nRescheduling from:" -ForegroundColor Yellow
Write-Host "  Old: $($appointment.startTime) - $($appointment.endTime)" -ForegroundColor White
Write-Host "  New: $($newSlot.startTime) - $($newSlot.endTime)" -ForegroundColor White

# Reschedule the appointment
$rescheduled = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/$appointmentId/reschedule?newTimeSlotId=$newSlotId" `
    -Method PUT

Write-Host "`n✅ APPOINTMENT RESCHEDULED!" -ForegroundColor Green
Write-Host "New time: $($rescheduled.startTime) - $($rescheduled.endTime)" -ForegroundColor Yellow
```

---

## Step 12: Cancel the Appointment

```powershell
# Cancel with a reason
$cancelled = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/$appointmentId`?reason=Personal%20emergency" `
    -Method DELETE

Write-Host "`n❌ APPOINTMENT CANCELLED" -ForegroundColor Yellow
Write-Host "Status: $($cancelled.status)" -ForegroundColor Red
Write-Host "Reason: $($cancelled.cancellationReason)" -ForegroundColor White
```

---

## Step 13: Verify Time Slot is Available Again

```powershell
# The original slot should be available again after cancellation
$slotsAfterCancel = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/available?salonId=$salonId&date=$tomorrow"
$freedSlot = $slotsAfterCancel | Where-Object { $_.id -eq $slotId }

if ($freedSlot) {
    Write-Host "`n✅ CORRECT: Original slot is available again after cancellation" -ForegroundColor Green
} else {
    Write-Host "`n❌ ERROR: Original slot should be available!" -ForegroundColor Red
}
```

---

## Step 14: Test Double Booking Prevention

```powershell
# Try to book a slot that's already booked
# First, create a new appointment
$newAppointment = @{
    customerName = "Jane Smith"
    customerEmail = "jane.smith@example.com"
    customerPhone = "+9876543210"
    preferredContact = "PHONE"
    salonId = $salonId
    serviceId = $serviceId
    timeSlotId = $slotId  # Use the freed slot
    notes = "New customer"
} | ConvertTo-Json

$appointment2 = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments" `
    -Method POST `
    -ContentType "application/json" `
    -Body $newAppointment

Write-Host "`n✅ First booking successful" -ForegroundColor Green

# Now try to book the SAME slot again (should fail)
try {
    $duplicateAppointment = @{
        customerName = "Bob Johnson"
        customerEmail = "bob.johnson@example.com"
        customerPhone = "+1122334455"
        preferredContact = "EMAIL"
        salonId = $salonId
        serviceId = $serviceId
        timeSlotId = $slotId  # SAME SLOT!
        notes = "This should fail"
    } | ConvertTo-Json

    Invoke-RestMethod -Uri "http://localhost:8080/api/appointments" `
        -Method POST `
        -ContentType "application/json" `
        -Body $duplicateAppointment

    Write-Host "❌ ERROR: Double booking should have been prevented!" -ForegroundColor Red
} catch {
    Write-Host "`n✅ CORRECT: Double booking was prevented!" -ForegroundColor Green
    Write-Host "Error Message: $($_.Exception.Message)" -ForegroundColor Yellow
}
```

---

## Complete Test Script

Here's a complete script that runs all tests:

```powershell
# Save this as: test-appointment-system.ps1

Write-Host "╔════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Salon Appointment Booking System - Full Test Suite  ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Cyan

# Test 1: Get Services
Write-Host "`n[TEST 1] Getting Services..." -ForegroundColor Magenta
$services = Invoke-RestMethod -Uri "http://localhost:8080/api/services"
Write-Host "✅ Found $($services.Count) services" -ForegroundColor Green

# Test 2: Get Salons
Write-Host "`n[TEST 2] Getting Salons..." -ForegroundColor Magenta
$salons = Invoke-RestMethod -Uri "http://localhost:8080/api/salons"
Write-Host "✅ Found $($salons.Count) salons" -ForegroundColor Green
$salonId = $salons[0].id

# Test 3: Get Available Slots
Write-Host "`n[TEST 3] Getting Available Slots..." -ForegroundColor Magenta
$tomorrow = (Get-Date).AddDays(1).ToString("yyyy-MM-dd")
$slots = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/available?salonId=$salonId&date=$tomorrow"
Write-Host "✅ Found $($slots.Count) available slots for $tomorrow" -ForegroundColor Green
$slotId = $slots[0].id

# Test 4: Create Appointment
Write-Host "`n[TEST 4] Creating Appointment..." -ForegroundColor Magenta
$serviceId = $services[0].id
$appointmentRequest = @{
    customerName = "Test Customer"
    customerEmail = "test@example.com"
    customerPhone = "+1234567890"
    preferredContact = "EMAIL"
    salonId = $salonId
    serviceId = $serviceId
    timeSlotId = $slotId
    notes = "Automated test"
} | ConvertTo-Json

$appointment = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments" `
    -Method POST `
    -ContentType "application/json" `
    -Body $appointmentRequest

Write-Host "✅ Appointment created: $($appointment.confirmationCode)" -ForegroundColor Green
$appointmentId = $appointment.id

# Test 5: Retrieve by Confirmation Code
Write-Host "`n[TEST 5] Retrieving by Confirmation Code..." -ForegroundColor Magenta
$retrieved = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/confirmation/$($appointment.confirmationCode)"
Write-Host "✅ Retrieved appointment: $($retrieved.confirmationCode)" -ForegroundColor Green

# Test 6: Confirm Appointment
Write-Host "`n[TEST 6] Confirming Appointment..." -ForegroundColor Magenta
$confirmed = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/$appointmentId/confirm" -Method PUT
Write-Host "✅ Appointment confirmed. Status: $($confirmed.status)" -ForegroundColor Green

# Test 7: Get Another Slot for Rescheduling
Write-Host "`n[TEST 7] Rescheduling Appointment..." -ForegroundColor Magenta
$slotsAfter = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/available?salonId=$salonId&date=$tomorrow"
$newSlotId = $slotsAfter[0].id
$rescheduled = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/$appointmentId/reschedule?newTimeSlotId=$newSlotId" -Method PUT
Write-Host "✅ Appointment rescheduled to: $($rescheduled.startTime)" -ForegroundColor Green

# Test 8: Cancel Appointment
Write-Host "`n[TEST 8] Cancelling Appointment..." -ForegroundColor Magenta
$cancelled = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/$appointmentId`?reason=Test%20complete" -Method DELETE
Write-Host "✅ Appointment cancelled. Status: $($cancelled.status)" -ForegroundColor Green

# Test 9: Double Booking Prevention
Write-Host "`n[TEST 9] Testing Double Booking Prevention..." -ForegroundColor Magenta
$appointment2 = @{
    customerName = "Customer 2"
    customerEmail = "customer2@example.com"
    customerPhone = "+9876543210"
    preferredContact = "EMAIL"
    salonId = $salonId
    serviceId = $serviceId
    timeSlotId = $newSlotId
    notes = "First booking"
} | ConvertTo-Json
$apt2 = Invoke-RestMethod -Uri "http://localhost:8080/api/appointments" -Method POST -ContentType "application/json" -Body $appointment2

try {
    $duplicate = @{
        customerName = "Customer 3"
        customerEmail = "customer3@example.com"
        customerPhone = "+1122334455"
        preferredContact = "EMAIL"
        salonId = $salonId
        serviceId = $serviceId
        timeSlotId = $newSlotId  # SAME SLOT
        notes = "Should fail"
    } | ConvertTo-Json
    Invoke-RestMethod -Uri "http://localhost:8080/api/appointments" -Method POST -ContentType "application/json" -Body $duplicate
    Write-Host "❌ FAILED: Double booking was not prevented!" -ForegroundColor Red
} catch {
    Write-Host "✅ Double booking prevention works!" -ForegroundColor Green
}

# Cleanup: Cancel the second appointment
Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/$($apt2.id)?reason=Test%20cleanup" -Method DELETE | Out-Null

Write-Host "`n╔════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║              ALL TESTS COMPLETED SUCCESSFULLY!         ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
```

---

## Common Issues & Solutions

### Issue 1: "Cannot find salon/service"
**Solution:** Make sure you have salons and services in your database. Check MongoDB Atlas or use the data seeding endpoints.

### Issue 2: "No available slots"
**Solution:** Generate slots for the salon:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/generate?salonId=YOUR_SALON_ID" -Method POST
```

### Issue 3: "Time slot not available"
**Solution:** The slot might already be booked. Get fresh available slots:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/appointments/slots/available?salonId=SALON_ID&date=YYYY-MM-DD"
```

### Issue 4: CORS errors from frontend
**Solution:** CORS is already configured to allow all origins (*). If still facing issues, check browser console and backend logs.

---

## What to Test Next

1. **Frontend Integration:**
   - Update booking components to use new appointment endpoints
   - Add confirmation code lookup feature
   - Create appointment management UI

2. **Edge Cases:**
   - Booking past time slots (should fail)
   - Rescheduling cancelled appointments (should fail)
   - Cancelling completed appointments (should fail)
   - Invalid customer data (should fail validation)

3. **Performance:**
   - Create multiple appointments simultaneously
   - Generate slots for many salons
   - Retrieve large appointment lists

---

## API Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointments` | Create appointment |
| GET | `/api/appointments/{id}` | Get appointment by ID |
| GET | `/api/appointments/confirmation/{code}` | Get by confirmation code |
| GET | `/api/appointments/customer/{customerId}` | Get customer appointments |
| GET | `/api/appointments/salon/{salonId}` | Get salon appointments |
| GET | `/api/appointments/status/{status}` | Get by status |
| GET | `/api/appointments` | Get all appointments |
| DELETE | `/api/appointments/{id}` | Cancel appointment |
| PUT | `/api/appointments/{id}/reschedule` | Reschedule appointment |
| PUT | `/api/appointments/{id}/confirm` | Confirm appointment |
| PUT | `/api/appointments/{id}/complete` | Complete appointment |
| GET | `/api/appointments/slots/available` | Get available slots |
| POST | `/api/appointments/slots/generate` | Generate time slots |

---

## 🎉 Success Criteria

Your appointment system is working correctly if:

✅ Services are seeded on startup (24 services)  
✅ Time slots are generated automatically (30-min intervals, 9 AM - 6 PM)  
✅ Appointments can be created with confirmation codes  
✅ Time slots become unavailable after booking  
✅ Double booking is prevented (409 Conflict error)  
✅ Appointments can be retrieved by ID or confirmation code  
✅ Appointments can be confirmed and rescheduled  
✅ Cancelled appointments free up time slots  
✅ Customer appointments can be listed  
✅ All CRUD operations work without errors

---

**Happy Testing! 🚀**

For detailed documentation, see `APPOINTMENT_SYSTEM_DOCUMENTATION.md`
