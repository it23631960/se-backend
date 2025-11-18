# Salon Booking Database Structure & Sample Data Guide

## 📋 Collections Overview

Your MongoDB database has **7 collections** with the following record counts for realistic testing:

| Collection | Records | Purpose |
|------------|---------|---------|
| **users** | 10 | Application users (customers/admins) |
| **salons** | 6 | Salon businesses with services |
| **services** | 30 | Services offered (5 per salon) |
| **customers** | 15 | Customer profiles for bookings |
| **time_slots** | 756 | Available appointment slots (7 days, 6 salons) |
| **appointments** | 25 | Booking records (past & upcoming) |
| **reviews** | 48 | Salon reviews & ratings |

---

## 🔗 Data Insertion Order

**CRITICAL:** Insert collections in this exact order to satisfy dependencies:

```
1. users          (no dependencies)
2. salons         (no dependencies)
3. customers      (no dependencies)
4. services       (needs salon IDs)
5. time_slots     (needs salon IDs)
6. appointments   (needs customer, salon, service, timeSlot IDs)
7. reviews        (needs salon IDs, optional user IDs)
```

---

## 📊 Collection Details

### 1. **Users Collection** (`users`)

#### Purpose
Registered application users (customers who can login, admins)

#### Fields
```json
{
  "_id": "ObjectId or custom string",
  "username": "string (required, min 2 chars)",
  "email": "string (required, valid email)",
  "password": "string (required - plain text for testing, BCrypt for production)"
}
```

#### Field Details
- **id**: Auto-generated MongoDB ObjectId or custom string like `"user1"`
- **username**: Display name, must be unique and 2+ characters
- **email**: Must be valid email format, unique across users
- **password**: 
  - For **testing**: use simple passwords like `"password123"`
  - For **production**: use BCrypt hashed passwords
  
#### Sample Values
```json
{
  "_id": "user1",
  "username": "sarahj",
  "email": "sarah.johnson@gmail.com",
  "password": "password123"
}
```

#### Relationships
- **Used by**: `Review.user` (optional @DBRef)
- **Used by**: Authentication/authorization systems

#### Recommended Count
- **Minimum**: 3 users (1 admin, 2 customers)
- **Realistic**: 10 users (mix of active customers)

#### Notes
- Some customers may not have user accounts (guest bookings)
- Passwords shown as plain text for testing only
- In production, use Spring Security BCrypt for password hashing

---

### 2. **Salons Collection** (`salons`)

#### Purpose
Salon business information, services offered, operating hours

#### Fields
```json
{
  "_id": "ObjectId or custom string",
  "name": "string (required, salon name)",
  "description": "string (detailed description)",
  "bannerImage": "string (image URL)",
  "images": ["array of image URLs"],
  "reviews": ["array of review IDs - DEPRECATED, use Review collection"],
  "address": "string (full address)",
  "phone": "string (Sri Lankan format: +94XXXXXXXXX)",
  "email": "string (valid email)",
  "services": ["array of service names - DEPRECATED, use Service collection"],
  "openTime": "string (HH:MM format, e.g., '09:00')",
  "closeTime": "string (HH:MM format, e.g., '18:00')",
  "available": "boolean (is salon accepting bookings)",
  "manager": "string (manager name or user ID)",
  "bookings": ["array of appointment IDs - DEPRECATED"],
  "slotsBooked": ["array of booked slot IDs - DEPRECATED"],
  "averageRating": "number (0.0-5.0, cached from reviews)",
  "totalReviews": "number (count of reviews, cached)"
}
```

#### Field Details
- **name**: Business name (e.g., "Elite Hair Studio")
- **description**: 100-300 character description of services/specialties
- **bannerImage**: Main hero image URL (use placeholder: `https://images.unsplash.com/photo-...`)
- **images**: Array of 2-5 additional images
- **address**: Full Sri Lankan address (e.g., "123 Galle Road, Colombo 03, Western Province, Sri Lanka")
- **phone**: Sri Lankan format `+94112345678` or `0112345678`
- **openTime/closeTime**: Operating hours in 24-hour format
- **available**: `true` if accepting bookings, `false` if temporarily closed
- **averageRating**: Cached average (updated by ReviewService)
- **totalReviews**: Cached count (updated by ReviewService)

#### Sample Values
```json
{
  "_id": "salon1",
  "name": "Elite Hair Studio",
  "description": "Premium hair care services in the heart of Colombo. Specializing in modern cuts, coloring, and bridal hair styling.",
  "bannerImage": "https://images.unsplash.com/photo-1560066984-138dadb4c035",
  "images": [
    "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f",
    "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e"
  ],
  "reviews": [],
  "address": "456 Galle Road, Colombo 03, Western Province, Sri Lanka",
  "phone": "+94112345001",
  "email": "info@elitehair.lk",
  "services": [],
  "openTime": "09:00",
  "closeTime": "18:00",
  "available": true,
  "manager": "Priya Fernando",
  "bookings": [],
  "slotsBooked": [],
  "averageRating": 4.7,
  "totalReviews": 12
}
```

#### Relationships
- **Referenced by**: `Service.salon` (@DBRef)
- **Referenced by**: `TimeSlot.salon` (@DBRef)
- **Referenced by**: `Appointment.salon` (@DBRef)
- **Referenced by**: `Review.salon` (@DBRef)

#### Recommended Count
- **Minimum**: 3 salons (test variety)
- **Realistic**: 6 salons (different cities/specialties)

#### Sri Lankan Cities to Use
- Colombo (Western Province) - Capital, most businesses
- Kandy (Central Province) - Cultural city
- Galle (Southern Province) - Coastal city
- Negombo (Western Province) - Tourist area
- Jaffna (Northern Province) - Northern city
- Matara (Southern Province) - Southern city

#### Salon Types & Specialties
- **Hair Salons**: Cuts, coloring, styling
- **Nail Salons**: Manicures, pedicures, nail art
- **Spa Salons**: Facials, massages, body treatments
- **Bridal Salons**: Wedding hair & makeup
- **Unisex Salons**: Services for all genders
- **Men's Grooming**: Barber services, beard styling

---

### 3. **Services Collection** (`services`)

#### Purpose
Individual services offered by salons (haircuts, coloring, spa treatments, etc.)

#### Fields
```json
{
  "_id": "ObjectId or custom string",
  "name": "string (required, service name)",
  "description": "string (detailed description)",
  "price": "number (required, in LKR, positive)",
  "durationMinutes": "number (required, positive, e.g., 30, 60)",
  "category": "string (HAIRCUT, COLORING, SPA, NAILS, BRIDAL, STYLING, etc.)",
  "active": "boolean (default true, is service currently offered)"
}
```

#### Field Details
- **name**: Service name (e.g., "Men's Haircut", "Full Body Massage")
- **description**: What's included, what to expect
- **price**: In Sri Lankan Rupees (LKR), typical ranges:
  - Basic haircut: Rs. 1,000 - 2,000
  - Premium haircut: Rs. 2,500 - 5,000
  - Hair coloring: Rs. 5,000 - 15,000
  - Bridal package: Rs. 25,000 - 75,000
  - Manicure: Rs. 1,000 - 2,000
  - Spa treatment: Rs. 3,000 - 10,000
- **durationMinutes**: How long service takes (align with time slot durations)
- **category**: For filtering/organizing services
- **active**: Set to `false` to temporarily disable a service

#### Sample Values
```json
{
  "_id": "service1",
  "name": "Men's Haircut",
  "description": "Professional men's haircut with wash and basic styling",
  "price": 1500,
  "durationMinutes": 30,
  "category": "HAIRCUT",
  "active": true
}
```

#### Service Categories
- **HAIRCUT**: Cuts, trims, shaves
- **COLORING**: Full color, highlights, balayage
- **STYLING**: Blowouts, updos, braids
- **SPA**: Facials, massages, body treatments
- **NAILS**: Manicures, pedicures, nail art
- **BRIDAL**: Wedding hair & makeup packages
- **THREADING**: Eyebrow, upper lip threading
- **WAXING**: Various waxing services

#### Common Services with Pricing
```
Men's Haircut          : Rs. 1,000 - 2,000   (30 min)
Women's Haircut        : Rs. 1,500 - 3,500   (45 min)
Hair Coloring (Full)   : Rs. 8,000 - 15,000  (120 min)
Highlights/Balayage    : Rs. 10,000 - 20,000 (150 min)
Hair Spa Treatment     : Rs. 3,000 - 7,000   (60 min)
Keratin Treatment      : Rs. 15,000 - 30,000 (180 min)
Bridal Hair & Makeup   : Rs. 25,000 - 75,000 (240 min)
Manicure               : Rs. 1,000 - 2,000   (45 min)
Pedicure               : Rs. 1,500 - 2,500   (60 min)
Gel Nails              : Rs. 3,000 - 5,000   (90 min)
Facial (Basic)         : Rs. 2,000 - 4,000   (60 min)
Facial (Premium)       : Rs. 5,000 - 10,000  (90 min)
Full Body Massage      : Rs. 4,000 - 8,000   (90 min)
Threading (Eyebrows)   : Rs. 300 - 500       (15 min)
Threading (Face)       : Rs. 800 - 1,200     (30 min)
```

#### Relationships
- **Belongs to**: Salon (implicitly, services are salon-specific)
- **Referenced by**: `Appointment.service` (@DBRef)

#### Recommended Count
- **Minimum**: 10 services (2-3 per salon)
- **Realistic**: 30 services (5 per salon, 6 salons)

#### Notes
- Each salon should have 3-7 services
- Mix of quick services (15-30 min) and long services (2-4 hours)
- Prices should reflect salon's positioning (budget/mid-range/premium)
- **IMPORTANT**: Services are NOT linked to salons in the Service model
  - To get services for a salon, query: `Service.find({})` and filter by salon context
  - **OR** you can add a `@DBRef private Salon salon;` field (recommended for clarity)

---

### 4. **Customers Collection** (`customers`)

#### Purpose
Customer profiles for appointment bookings (both registered users and guests)

#### Fields
```json
{
  "_id": "ObjectId or custom string",
  "name": "string (required, 2+ chars)",
  "email": "string (required, valid email, unique)",
  "phone": "string (required, Sri Lankan format: +94XXXXXXXXX or 0XXXXXXXXX)",
  "createdAt": "ISO date-time (auto-generated)",
  "preferredContact": "string (EMAIL, PHONE, SMS)",
  "notes": "string (internal notes about customer)"
}
```

#### Field Details
- **name**: Full name (e.g., "Sarah Johnson", "Kumari Perera")
- **email**: Must be unique, used for booking confirmations
- **phone**: Sri Lankan format:
  - International: `+94771234567` (preferred)
  - Local: `0771234567`
  - Pattern: `^(\\+94|0)[0-9]{9}$`
- **createdAt**: Auto-set to current timestamp
- **preferredContact**: How customer wants to be reached
- **notes**: Internal notes (e.g., "Prefers morning appointments", "Allergic to certain products")

#### Sample Values
```json
{
  "_id": "customer1",
  "name": "Sarah Johnson",
  "email": "sarah.johnson@gmail.com",
  "phone": "+94771234567",
  "createdAt": "2025-09-15T10:30:00Z",
  "preferredContact": "EMAIL",
  "notes": "Regular customer, prefers stylist Priya"
}
```

#### Relationships
- **Referenced by**: `Appointment.customer` (@DBRef)
- **NOT linked to**: `User` (customers can exist without user accounts)

#### Recommended Count
- **Minimum**: 5 customers
- **Realistic**: 15 customers (mix of regulars and new)

#### Name Suggestions (Sri Lankan Context)
**Sinhala Names:**
- Priya Fernando, Kumari Perera, Nimal Silva, Anjali Wickramasinghe
- Chaminda Rodrigo, Dilshan Jayawardena, Sanduni Mendis

**Tamil Names:**
- Ananya Subramaniam, Rajesh Kumar, Kavitha Sivakumar, Arun Nadarajah

**Muslim Names:**
- Farah Mohamed, Rizan Farook, Zainab Ismail

**International Names:**
- Sarah Johnson, Michael Williams, Emily Chen, David Brown

---

### 5. **TimeSlots Collection** (`time_slots`)

#### Purpose
Available appointment time slots for each salon

#### Fields
```json
{
  "_id": "ObjectId or custom string",
  "date": "LocalDate (ISO format: YYYY-MM-DD)",
  "startTime": "LocalTime (HH:MM format)",
  "endTime": "LocalTime (HH:MM format)",
  "isAvailable": "boolean (default true, false if booked)",
  "salon": "DBRef to Salon (required)"
}
```

#### Field Details
- **date**: Date of the slot (e.g., `"2025-10-15"`)
- **startTime**: Start time in 24-hour format (e.g., `"09:00"`, `"14:30"`)
- **endTime**: End time (e.g., `"09:30"`, `"15:00"`)
- **isAvailable**: `true` if slot can be booked, `false` if already booked
- **salon**: Reference to the salon this slot belongs to

#### Sample Values
```json
{
  "_id": "slot1",
  "date": "2025-10-15",
  "startTime": "09:00",
  "endTime": "09:30",
  "isAvailable": true,
  "salon": {
    "$ref": "salons",
    "$id": "salon1"
  }
}
```

#### Generation Logic
**For realistic testing, generate slots for:**
- **Date range**: Next 7 days (Oct 11-17, 2025)
- **Time range**: 9:00 AM - 6:00 PM (salon operating hours)
- **Slot duration**: 30 minutes
- **Slots per day**: 18 slots (9 hours × 2 slots per hour)
- **Skip lunch**: 1:00 PM - 2:00 PM (2 slots)
- **Effective slots per day**: 16 slots
- **Total slots per salon**: 16 slots × 7 days = 112 slots
- **Total slots (6 salons)**: 112 × 6 = 672 slots

#### Availability Distribution
- **70% available** (isAvailable: true) - ~470 slots
- **30% booked** (isAvailable: false) - ~202 slots

#### Time Slot Schedule
```
09:00 - 09:30 ✓
09:30 - 10:00 ✓
10:00 - 10:30 ✓
10:30 - 11:00 ✓
11:00 - 11:30 ✓
11:30 - 12:00 ✓
12:00 - 12:30 ✓
12:30 - 13:00 ✓
13:00 - 13:30 ✗ LUNCH
13:30 - 14:00 ✗ LUNCH
14:00 - 14:30 ✓
14:30 - 15:00 ✓
15:00 - 15:30 ✓
15:30 - 16:00 ✓
16:00 - 16:30 ✓
16:30 - 17:00 ✓
17:00 - 17:30 ✓
17:30 - 18:00 ✓
```

#### Relationships
- **Belongs to**: `Salon` (@DBRef, required)
- **Referenced by**: `Appointment.timeSlot` (@DBRef)
- **Compound Index**: `{date, startTime, salon}` (unique combination)

#### Recommended Count
- **Minimum**: 50 slots (for basic testing)
- **Realistic**: 672 slots (7 days × 6 salons × 16 slots/day)

#### Notes
- Compound index ensures no duplicate slots for same salon/date/time
- When appointment is confirmed, mark `isAvailable = false`
- Past slots should remain in database for historical records
- Generate new slots automatically as dates approach

---

### 6. **Appointments Collection** (`appointments`)

#### Purpose
Booking records linking customers, salons, services, and time slots

#### Fields
```json
{
  "_id": "ObjectId or custom string",
  "customer": "DBRef to Customer (required)",
  "service": "DBRef to Service (required)",
  "timeSlot": "DBRef to TimeSlot (required)",
  "salon": "DBRef to Salon (required)",
  "bookingDate": "ISO date-time (when booking was made)",
  "status": "enum (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)",
  "notes": "string (customer's special requests/notes)",
  "price": "number (service price at time of booking)",
  "confirmationCode": "string (unique booking reference, e.g., 'BK-2025-001')",
  "assignedStaff": "string (staff member name)",
  "lastModifiedDate": "ISO date-time (last update timestamp)",
  "cancellationReason": "string (reason if cancelled)"
}
```

#### Field Details
- **customer**: Reference to Customer who made the booking
- **service**: Reference to the booked Service
- **timeSlot**: Reference to the booked TimeSlot (must have `isAvailable: false`)
- **salon**: Reference to the Salon (for convenience, derived from service/timeSlot)
- **bookingDate**: When customer made the booking (auto-set to current time)
- **status**: Current state of appointment (see Status Types below)
- **notes**: Customer requests (e.g., "First time customer", "Prefers quiet area")
- **price**: Service price at booking time (may differ from current service price)
- **confirmationCode**: Unique reference like `"BK-2025-001"`, `"APPT-10251530"`
- **assignedStaff**: Staff member assigned to this appointment
- **lastModifiedDate**: Track when appointment was last updated
- **cancellationReason**: Required if status is CANCELLED

#### Status Types (AppointmentStatus Enum)
- **PENDING**: Appointment requested, awaiting confirmation
- **CONFIRMED**: Salon confirmed the appointment
- **CANCELLED**: Customer or salon cancelled
- **COMPLETED**: Appointment finished successfully
- **NO_SHOW**: Customer didn't show up

#### Sample Values
```json
{
  "_id": "appt1",
  "customer": {
    "$ref": "customers",
    "$id": "customer1"
  },
  "service": {
    "$ref": "services",
    "$id": "service1"
  },
  "timeSlot": {
    "$ref": "time_slots",
    "$id": "slot1"
  },
  "salon": {
    "$ref": "salons",
    "$id": "salon1"
  },
  "bookingDate": "2025-10-08T14:30:00Z",
  "status": "CONFIRMED",
  "notes": "First time customer, prefers quieter area",
  "price": 1500,
  "confirmationCode": "BK-2025-001",
  "assignedStaff": "Priya Fernando",
  "lastModifiedDate": "2025-10-08T14:35:00Z",
  "cancellationReason": null
}
```

#### Status Distribution (for 25 appointments)
- **5 COMPLETED** (past dates, Oct 1-7)
- **12 CONFIRMED** (upcoming dates, Oct 11-17)
- **3 PENDING** (recent bookings awaiting confirmation)
- **3 CANCELLED** (mix of dates)
- **2 NO_SHOW** (past dates)

#### Relationships
- **References**: Customer, Service, TimeSlot, Salon (all @DBRef)
- **Referenced by**: `Review.appointment` (optional @DBRef for verified reviews)

#### Recommended Count
- **Minimum**: 10 appointments
- **Realistic**: 25 appointments (mix of statuses and dates)

#### Important Notes
- When creating an appointment with status CONFIRMED:
  - **MUST** mark corresponding `TimeSlot.isAvailable = false`
- When cancelling an appointment:
  - **MUST** mark corresponding `TimeSlot.isAvailable = true`
- **price** field preserves original service price (services prices may change)
- **confirmationCode** should be unique and easy to communicate (avoid 0/O, 1/I confusion)

---

### 7. **Reviews Collection** (`reviews`)

#### Purpose
Customer reviews and ratings for salons

#### Fields
```json
{
  "_id": "ObjectId or custom string",
  "salon": "DBRef to Salon (required)",
  "user": "DBRef to User (optional, null for guest reviews)",
  "reviewerName": "string (required, 2-100 chars)",
  "reviewerEmail": "string (required, valid email)",
  "rating": "integer (required, 1-5 stars)",
  "comment": "string (required, 10-500 chars)",
  "reviewDate": "ISO date-time (when review was created)",
  "lastModified": "ISO date-time (when review was last edited)",
  "isVerified": "boolean (true if from actual appointment)",
  "appointment": "DBRef to Appointment (if verified review)",
  "isVisible": "boolean (true if publicly visible, false if hidden by moderator)",
  "helpfulCount": "integer (number of 'helpful' votes)",
  "reportCount": "integer (number of spam reports)",
  "moderatorNotes": "string (internal moderation notes)",
  "ownerResponse": "string (salon owner's response)",
  "ownerResponseDate": "ISO date-time (when owner responded)"
}
```

#### Field Details
- **salon**: Which salon is being reviewed (required)
- **user**: Registered user who wrote review (null if guest)
- **reviewerName**: Display name (from User or guest input)
- **reviewerEmail**: Contact email (from User or guest input)
- **rating**: 1-5 stars (integer, required)
- **comment**: Written feedback, 10-500 characters
- **reviewDate**: When review was posted (auto-set)
- **lastModified**: Null if never edited, set when review is updated
- **isVerified**: True if review linked to completed appointment
- **appointment**: Reference to appointment (for verified reviews only)
- **isVisible**: False if hidden by moderators (spam/inappropriate)
- **helpfulCount**: Users can vote reviews as helpful
- **reportCount**: Users can report spam/inappropriate reviews
  - Auto-hidden if reportCount >= 5
- **moderatorNotes**: Internal notes (not shown to public)
- **ownerResponse**: Salon owner can respond to reviews
- **ownerResponseDate**: When owner posted response

#### Sample Values
```json
{
  "_id": "review1",
  "salon": {
    "$ref": "salons",
    "$id": "salon1"
  },
  "user": {
    "$ref": "users",
    "$id": "user1"
  },
  "reviewerName": "Sarah Johnson",
  "reviewerEmail": "sarah.johnson@gmail.com",
  "rating": 5,
  "comment": "Amazing service! The staff was very professional and friendly. My haircut turned out exactly as I wanted. The salon is clean and has a great atmosphere. Highly recommend!",
  "reviewDate": "2025-10-08T16:30:00Z",
  "lastModified": null,
  "isVerified": true,
  "appointment": {
    "$ref": "appointments",
    "$id": "appt1"
  },
  "isVisible": true,
  "helpfulCount": 12,
  "reportCount": 0,
  "moderatorNotes": null,
  "ownerResponse": "Thank you so much, Sarah! We're thrilled you loved your new look. We look forward to seeing you again soon!",
  "ownerResponseDate": "2025-10-09T09:15:00Z"
}
```

#### Rating Distribution (for 48 reviews across 6 salons)
- **60% = 5 stars** (29 reviews) - Excellent
- **25% = 4 stars** (12 reviews) - Good
- **10% = 3 stars** (5 reviews) - Average
- **5% = 1-2 stars** (2 reviews) - Poor

#### Review Types
- **Verified Reviews** (70%): Linked to completed appointments
  - `isVerified: true`
  - `appointment` field populated
  - More trustworthy
- **Guest Reviews** (30%): Not linked to user accounts
  - `user: null`
  - `isVerified: false`
  - Still valuable feedback

#### Relationships
- **References**: Salon (required @DBRef), User (optional @DBRef), Appointment (optional @DBRef)
- **Updates**: Salon's `averageRating` and `totalReviews` (cached)
- **Compound Index**: `{salon, user}` unique (prevents duplicate reviews from same user)

#### Recommended Count
- **Minimum**: 15 reviews (2-3 per salon)
- **Realistic**: 48 reviews (6-10 per salon, spread over 3 months)

#### Review Date Range
- Spread reviews over past 3 months: July-October 2025
- More recent reviews (October) should be more common
- Verified reviews should match completed appointment dates

#### Sample Review Comments
**5-Star Reviews:**
- "Amazing service! The staff was very professional and friendly..."
- "Best salon experience I've had in Colombo. The haircut exceeded my expectations..."
- "Absolutely loved my bridal makeup! The team was patient and skilled..."

**4-Star Reviews:**
- "Great service overall, but had to wait 15 minutes past my appointment time..."
- "Good haircut, but the salon was a bit noisy during my visit..."

**3-Star Reviews:**
- "Decent service but nothing exceptional. Staff was friendly but rushed..."

**1-2 Star Reviews:**
- "Disappointed with the hair coloring result. Not what I asked for..."

#### Important Notes
- **One review per user per salon** (enforced by compound unique index)
- Reviews can be edited within **24 hours** of posting (use `isEditable()` method)
- Reviews with **5+ reports** are auto-hidden (`isVisible: false`)
- Guest reviews don't prevent duplicate reviews (no user ID to check)
- When review is added/updated/deleted:
  - **MUST** update `Salon.averageRating` and `Salon.totalReviews`
  - Use `ReviewService.updateSalonRatingAsync()` method

---

## 🔑 ID Mapping Reference

### Salon IDs
```
salon1 = "Elite Hair Studio" (Colombo)
salon2 = "Golden Locks Salon" (Kandy)
salon3 = "Style Avenue Spa" (Galle)
salon4 = "Royal Bridal Salon" (Negombo)
salon5 = "Trendy Cuts Unisex" (Jaffna)
salon6 = "Ocean Breeze Salon" (Matara)
```

### User IDs (Sample - 10 users)
```
user1 = sarah.johnson@gmail.com (Sarah Johnson)
user2 = priya.fernando@gmail.com (Priya Fernando)
user3 = michael.williams@yahoo.com (Michael Williams)
user4 = kumari.perera@gmail.com (Kumari Perera)
user5 = david.silva@outlook.com (David Silva)
user6 = ananya.subramaniam@gmail.com (Ananya Subramaniam)
user7 = nimal.jayawardena@gmail.com (Nimal Jayawardena)
user8 = emily.chen@gmail.com (Emily Chen)
user9 = rajesh.kumar@yahoo.com (Rajesh Kumar)
user10 = farah.mohamed@gmail.com (Farah Mohamed)
```

### Customer IDs (Sample - 15 customers)
```
customer1-10 = Same as users above (registered customers)
customer11 = Guest: Amanda Thompson
customer12 = Guest: Sanduni Mendis
customer13 = Guest: James Anderson
customer14 = Guest: Kavitha Sivakumar
customer15 = Guest: Daniel Brown
```

### Service IDs (5 per salon = 30 total)
```
Salon 1 (Elite Hair Studio):
  service1 = Men's Haircut (Rs. 1,500)
  service2 = Women's Haircut (Rs. 2,500)
  service3 = Hair Coloring (Rs. 8,000)
  service4 = Hair Spa (Rs. 4,000)
  service5 = Keratin Treatment (Rs. 20,000)

Salon 2 (Golden Locks):
  service6 = Kids Haircut (Rs. 1,000)
  service7 = Bridal Package (Rs. 50,000)
  service8 = Highlights (Rs. 12,000)
  service9 = Blowdry & Style (Rs. 2,000)
  service10 = Hair Extensions (Rs. 15,000)

(Continue for salons 3-6...)
```

---

## 📈 Data Quality Guidelines

### Realistic Data Requirements

1. **Names**
   - Mix of Sri Lankan (Sinhala, Tamil, Muslim) and international names
   - Proper capitalization
   - No typos or formatting issues

2. **Emails**
   - Valid format: `name@domain.com`
   - Mix of Gmail, Yahoo, Outlook domains
   - Lowercase

3. **Phone Numbers**
   - Sri Lankan format: `+94771234567` (preferred)
   - Or local format: `0771234567`
   - Valid Sri Lankan mobile prefixes: 071, 077, 075, 076, 078

4. **Dates & Times**
   - ISO 8601 format for dates: `2025-10-15`
   - ISO 8601 for date-times: `2025-10-15T14:30:00Z`
   - LocalTime format: `09:00`, `14:30` (24-hour)
   - Consistent timezone (UTC or Asia/Colombo)

5. **Prices**
   - In Sri Lankan Rupees (LKR)
   - Whole numbers (no decimals needed)
   - Realistic ranges for services

6. **Review Comments**
   - Natural, conversational language
   - Specific details (not generic)
   - Mix of short (10-50 words) and detailed (100-200 words)
   - Professional tone

7. **Addresses**
   - Real Sri Lankan locations/roads
   - Format: Street, City, Province, Country
   - Include postal codes where appropriate

---

## ⚠️ Critical Relationships

### When Creating Appointments:
1. ✅ Ensure `Customer` exists
2. ✅ Ensure `Salon` exists
3. ✅ Ensure `Service` exists
4. ✅ Ensure `TimeSlot` exists
5. ✅ Verify `TimeSlot.isAvailable = true`
6. ✅ Set `Appointment.salon` to match `TimeSlot.salon`
7. ✅ After creating appointment with status CONFIRMED:
   - **Mark `TimeSlot.isAvailable = false`**

### When Creating Reviews:
1. ✅ Ensure `Salon` exists
2. ✅ Optional: Ensure `User` exists (if registered user review)
3. ✅ Optional: Ensure `Appointment` exists (if verified review)
4. ✅ If verified: Appointment status must be COMPLETED
5. ✅ After creating/updating/deleting review:
   - **Update `Salon.averageRating`** (calculate from all reviews)
   - **Update `Salon.totalReviews`** (count all reviews)

### Sample Rating Calculation:
```javascript
// For salon1 with reviews:
// Review 1: 5 stars
// Review 2: 4 stars
// Review 3: 5 stars
// Review 4: 3 stars

averageRating = (5 + 4 + 5 + 3) / 4 = 4.25
totalReviews = 4

salon1.averageRating = 4.25
salon1.totalReviews = 4
```

---

## 🎯 Testing Scenarios

### Scenario 1: Customer Books Appointment
1. Customer `customer1` wants to book:
   - Service: `service1` (Men's Haircut, Rs. 1,500)
   - Date: Oct 15, 2025
   - Time: 10:00-10:30
2. Find available time slot:
   - Query: `TimeSlot.find({ date: "2025-10-15", startTime: "10:00", isAvailable: true })`
3. Create appointment:
   - Link customer, service, timeSlot, salon
   - Set status: PENDING or CONFIRMED
4. Mark time slot unavailable:
   - Update: `TimeSlot.isAvailable = false`

### Scenario 2: Customer Leaves Review
1. Customer `customer1` completes appointment `appt1`
2. Update appointment status: COMPLETED
3. Customer submits 5-star review
4. Create Review document:
   - Link to salon1, user1, appt1
   - Set isVerified: true
5. Update salon rating cache:
   - Recalculate averageRating from all reviews
   - Increment totalReviews

### Scenario 3: Search Available Slots
1. User searches for salon1 on Oct 15, 2025
2. Query: `TimeSlot.find({ salon: "salon1", date: "2025-10-15", isAvailable: true })`
3. Display available slots: 09:00, 09:30, 11:00, 11:30, etc.
4. User selects 11:00-11:30
5. Create appointment and mark slot unavailable

---

## 📊 Summary Statistics

```
Database: salon_booking
Total Collections: 7
Total Documents: ~884

Documents per Collection:
├── users: 10
├── salons: 6
├── services: 30
├── customers: 15
├── time_slots: 672 (7 days × 6 salons × 16 slots/day)
├── appointments: 25
└── reviews: 48

Database Size: ~2-3 MB (with realistic data)
Index Count: 12 (compound and single field indexes)

Relationships:
├── Service → Salon (implicit, via context)
├── TimeSlot → Salon (@DBRef)
├── Appointment → Customer, Service, TimeSlot, Salon (@DBRef)
└── Review → Salon, User, Appointment (@DBRef)
```

---

## 🚀 Next Steps

1. **Review this structure** - Understand fields and relationships
2. **Choose insertion method**:
   - MongoDB Compass (GUI) - Easy for beginners
   - MongoDB Shell (CLI) - Fast for developers
   - Spring Boot DataInitializer - Production-ready
3. **Insert data** in correct order (users → salons → customers → services → time_slots → appointments → reviews)
4. **Verify data** using MongoDB Compass or queries
5. **Test application** with realistic sample data

---

📚 **Related Files:**
- `users.json` - Sample user data
- `salons.json` - Sample salon data
- `services.json` - Sample service data
- `customers.json` - Sample customer data
- `timeSlots.json` - Sample time slot data
- `appointments.json` - Sample appointment data
- `reviews.json` - Sample review data
- `mongo-insert-script.js` - MongoDB shell script
- `DataInitializer.java` - Spring Boot seeder
- `DATA_INSERTION_GUIDE.md` - Step-by-step instructions
