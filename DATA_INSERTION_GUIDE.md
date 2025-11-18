# 📘 Sample Data Insertion Guide

## Overview
This guide provides step-by-step instructions for inserting sample data into your MongoDB database using **three different methods**:

1. **MongoDB Compass** (GUI - Easiest for beginners)
2. **MongoDB Shell** (CLI - Fastest for bulk insertion)
3. **Spring Boot DataInitializer** (Automatic - Best for development)

**Total Sample Data:**
- 10 Users
- 6 Salons (across Sri Lanka: Colombo, Kandy, Galle, Negombo, Jaffna, Matara)
- 30 Services (haircut, coloring, spa, nails, bridal, etc.)
- 15 Customers
- 672 Time Slots (7 days × 6 salons × 16 slots/day)
- 25 Appointments (all statuses: PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)
- 48 Reviews (realistic ratings and comments)

**Total Documents:** 806 documents across 7 collections

---

## Prerequisites

### For All Methods:
- MongoDB installed and running (version 4.4+ recommended)
- Database name: `salon_booking` (will be created automatically)
- Sample data files downloaded from: `backend/sample-data/`

### For Method 1 (MongoDB Compass):
- Download MongoDB Compass: https://www.mongodb.com/try/download/compass
- Connect to your MongoDB instance (default: `mongodb://localhost:27017`)

### For Method 2 (MongoDB Shell):
- MongoDB Shell (`mongosh`) installed: https://www.mongodb.com/try/download/shell
- Sample data files in `backend/sample-data/` directory

### For Method 3 (Spring Boot):
- Spring Boot project set up with all dependencies
- MongoDB connection configured in `application.properties`
- All model classes and repositories present

---

## Method 1: MongoDB Compass (GUI) 🖱️

**Best for:** Beginners, visual learners, manual control

### Step 1: Connect to MongoDB

1. Open **MongoDB Compass**
2. Connect to your MongoDB instance:
   - Connection String: `mongodb://localhost:27017`
   - Click **Connect**

### Step 2: Create Database

1. Click **Create Database** button (green button at top)
2. Database Name: `salon_booking`
3. Collection Name: `users` (we'll create this first)
4. Click **Create Database**

### Step 3: Import Users (10 records)

1. Select `salon_booking` database → `users` collection
2. Click **Add Data** → **Import JSON or CSV file**
3. Select file: `backend/sample-data/1-users.json`
4. Input File Type: **JSON**
5. Click **Import**
6. Verify: Should see "10 documents successfully imported"

### Step 4: Import Salons (6 records)

1. Click **+** next to Collections → Create collection: `salons`
2. Click **Add Data** → **Import JSON or CSV file**
3. Select file: `backend/sample-data/2-salons.json`
4. Click **Import**
5. Verify: Should see "6 documents successfully imported"

### Step 5: Import Services (30 records)

1. Create collection: `services`
2. Import file: `backend/sample-data/3-services.json`
3. Verify: "30 documents successfully imported"

### Step 6: Import Customers (15 records)

1. Create collection: `customers`
2. Import file: `backend/sample-data/4-customers.json`
3. Verify: "15 documents successfully imported"

### Step 7: Import Time Slots (Sample - 16 records)

**Note:** The sample file only contains 16 sample time slots. For full 672 slots, use Method 2 or 3.

1. Create collection: `time_slots`
2. Import file: `backend/sample-data/5-timeslots-sample.json`
3. Verify: "16 documents successfully imported"
4. **For full data:** Use MongoDB Shell (Method 2) or DataInitializer (Method 3)

### Step 8: Import Appointments (25 records)

1. Create collection: `appointments`
2. Import file: `backend/sample-data/6-appointments.json`
3. Verify: "25 documents successfully imported"

### Step 9: Import Reviews (48 records)

1. Create collection: `reviews`
2. Import file: `backend/sample-data/7-reviews.json`
3. Verify: "48 documents successfully imported"

### Step 10: Verify Data

Run these queries in Compass (MongoDB Shell tab at bottom):

```javascript
// Check document counts
db.users.countDocuments()         // Should be 10
db.salons.countDocuments()        // Should be 6
db.services.countDocuments()      // Should be 30
db.customers.countDocuments()     // Should be 15
db.time_slots.countDocuments()    // Should be 16 (sample) or 672 (full)
db.appointments.countDocuments()  // Should be 25
db.reviews.countDocuments()       // Should be 48

// Check a sample document
db.salons.findOne({ _id: "salon1" })
```

**✅ Success!** Your database is now populated with sample data.

---

## Method 2: MongoDB Shell (CLI) 🖥️

**Best for:** Developers, automation, bulk insertion

### Step 1: Open MongoDB Shell

```powershell
# Open PowerShell and start MongoDB Shell
mongosh
```

### Step 2: Run Insertion Script

**Option A: Load script file** (Recommended)

```javascript
// Navigate to backend/sample-data directory first
cd "e:\Saloon\Hasith\backend\sample-data"

// Then in mongosh:
load('mongo-insert-script.js')
```

**Option B: Pipe script from PowerShell**

```powershell
# From PowerShell (not in mongosh)
cd "e:\Saloon\Hasith\backend\sample-data"
mongosh < mongo-insert-script.js
```

### Step 3: What the Script Does

The `mongo-insert-script.js` script automatically:

1. Switches to `salon_booking` database (creates if doesn't exist)
2. Inserts 10 users
3. Inserts 6 salons
4. Inserts 15 customers
5. Inserts 30 services
6. **Programmatically generates 672 time slots** (7 days × 6 salons × 16 slots/day)
   - Time range: 09:00-18:00 (skip 13:00-14:00 lunch)
   - 30-minute intervals
   - 70% available, 30% booked (random)
7. Provides verification queries
8. Prints summary of inserted documents

### Step 4: Import Appointments and Reviews

The script notes that appointments and reviews should be imported separately:

```powershell
# Still in backend/sample-data directory
mongoimport --db salon_booking --collection appointments --file 6-appointments.json --jsonArray
mongoimport --db salon_booking --collection reviews --file 7-reviews.json --jsonArray
```

### Step 5: Verify Insertion

```javascript
// In mongosh
use salon_booking

db.users.countDocuments()         // 10
db.salons.countDocuments()        // 6
db.services.countDocuments()      // 30
db.customers.countDocuments()     // 15
db.time_slots.countDocuments()    // 672
db.appointments.countDocuments()  // 25
db.reviews.countDocuments()       // 48

// Check sample data
db.salons.find().pretty()
db.time_slots.findOne()
```

**✅ Success!** All 806 documents inserted.

---

## Method 3: Spring Boot DataInitializer (Automatic) ⚙️

**Best for:** Development environment, automatic setup, first-time runs

### Step 1: Configure Application

Ensure `application.properties` has MongoDB connection:

```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/salon_booking
spring.data.mongodb.database=salon_booking

# Optional: Enable if you want to see MongoDB queries
logging.level.org.springframework.data.mongodb.core.MongoTemplate=DEBUG
```

### Step 2: Enable DataInitializer

The `DataInitializer.java` is already configured and will run automatically on application startup.

**Important:** It only runs if the database is **empty** (safe for production).

**File Location:**
```
backend/salon-booking/src/main/java/com/example/salon_booking/config/DataInitializer.java
```

### Step 3: Run Spring Boot Application

```powershell
# From backend/salon-booking directory
.\mvnw spring-boot:run

# OR if using IDE:
# Right-click Application.java → Run 'SalonBookingApplication'
```

### Step 4: Watch Console Output

You'll see logs like:

```
🚀 Starting Database Initialization...
📝 Database is empty. Starting data population...
📝 Inserting Users...
✅ Inserted 10 users
📝 Inserting Salons...
✅ Inserted 6 salons
📝 Inserting Customers...
✅ Inserted 15 customers
📝 Inserting Services...
✅ Inserted 30 services
📝 Generating Time Slots (7 days × 6 salons × 16 slots/day)...
✅ Generated 672 time slots
✅ Database initialization complete!

╔══════════════════════════════════════════════════════╗
║      DATABASE INITIALIZATION SUMMARY                 ║
╠══════════════════════════════════════════════════════╣
║  Users:         10 records                           ║
║  Salons:         6 records                           ║
║  Services:      30 records                           ║
║  Customers:     15 records                           ║
║  Time Slots:   672 records                          ║
║  Appointments:   0 records (use JSON for full data) ║
║  Reviews:        0 records (use JSON for full data) ║
╚══════════════════════════════════════════════════════╝

💡 TIP: For full appointments (25) and reviews (48), import JSON files:
   - backend/sample-data/6-appointments.json
   - backend/sample-data/7-reviews.json
   Use MongoDB Compass for easy import!
```

### Step 5: Import Appointments and Reviews (Optional)

The DataInitializer only inserts core data. For full appointments and reviews:

**Option A: Use MongoDB Compass**
- Import `6-appointments.json` into `appointments` collection
- Import `7-reviews.json` into `reviews` collection

**Option B: Use MongoDB Shell**
```powershell
mongoimport --db salon_booking --collection appointments --file backend\sample-data\6-appointments.json --jsonArray
mongoimport --db salon_booking --collection reviews --file backend\sample-data\7-reviews.json --jsonArray
```

### Step 6: Subsequent Runs

On subsequent application restarts:

```
🚀 Starting Database Initialization...
✅ Database already contains data. Skipping initialization.
```

**To force re-initialization:** Drop collections manually using MongoDB Compass or:

```javascript
// In mongosh
use salon_booking
db.dropDatabase()
```

Then restart the Spring Boot application.

---

## Verification Queries

### Count All Documents

```javascript
use salon_booking

db.users.countDocuments()         // Expected: 10
db.salons.countDocuments()        // Expected: 6
db.services.countDocuments()      // Expected: 30
db.customers.countDocuments()     // Expected: 15
db.time_slots.countDocuments()    // Expected: 672 (or 16 if using sample)
db.appointments.countDocuments()  // Expected: 25
db.reviews.countDocuments()       // Expected: 48

// Total: 806 documents
```

### Check Data Quality

```javascript
// Check users have correct fields
db.users.findOne({ _id: "user1" })

// Check salons in different cities
db.salons.find({}, { name: 1, address: 1 }).pretty()

// Check services by category
db.services.find({ category: "HAIRCUT" })

// Check time slots for a specific salon
db.time_slots.find({ "salon.$id": "salon1" }).limit(5)

// Check appointments by status
db.appointments.countDocuments({ status: "CONFIRMED" })  // Should be 12
db.appointments.countDocuments({ status: "COMPLETED" })  // Should be 5
db.appointments.countDocuments({ status: "PENDING" })    // Should be 3
db.appointments.countDocuments({ status: "CANCELLED" })  // Should be 3
db.appointments.countDocuments({ status: "NO_SHOW" })    // Should be 2

// Check reviews by rating
db.reviews.aggregate([
  { $group: { _id: "$rating", count: { $sum: 1 } } },
  { $sort: { _id: -1 } }
])
// Expected: Mostly 4-5 stars (60% 5-star, 25% 4-star)

// Verify DBRef relationships
db.appointments.findOne({}, { customer: 1, service: 1, timeSlot: 1, salon: 1 })
// Should show $ref and $id fields
```

### Check Data Integrity

```javascript
// Verify all salons have ratings
db.salons.find({ averageRating: { $exists: true } }).count()  // Should be 6

// Check time slots date range
db.time_slots.aggregate([
  { $group: { _id: null, minDate: { $min: "$date" }, maxDate: { $max: "$date" } } }
])
// Should span 7 days from tomorrow

// Verify customer emails match some user emails
db.customers.find({ email: "sarah.johnson@gmail.com" })  // Should exist
db.users.find({ email: "sarah.johnson@gmail.com" })      // Should exist

// Check appointment confirmation codes are unique
db.appointments.aggregate([
  { $group: { _id: "$confirmationCode", count: { $sum: 1 } } },
  { $match: { count: { $gt: 1 } } }
])
// Should return empty (no duplicates)
```

---

## Troubleshooting

### Issue 1: "Database already contains data"

**Problem:** DataInitializer skips insertion

**Solution:**
```javascript
// Drop database in mongosh
use salon_booking
db.dropDatabase()

// Then restart Spring Boot app
```

### Issue 2: "DBRef not resolving"

**Problem:** Appointments/Reviews show DBRef but don't load related data

**Solution:**
- This is normal MongoDB behavior. DBRef is manual reference.
- Spring Data MongoDB requires `@DBRef` annotation (already present in models)
- Use repository methods to fetch related data:

```java
// In Service layer
Appointment appointment = appointmentRepository.findById(id);
Customer customer = appointment.getCustomer(); // Spring resolves DBRef automatically
```

### Issue 3: Time Slots not generated (only 16)

**Problem:** Used sample file instead of script/DataInitializer

**Solution:**
- Use Method 2 (MongoDB Shell script) - generates 672 slots
- Or use Method 3 (DataInitializer) - generates 672 slots
- Or run manual generation in mongosh (see mongo-insert-script.js)

### Issue 4: Import fails with "duplicate key error"

**Problem:** Data already exists with same `_id`

**Solution:**
```javascript
// Drop specific collection
use salon_booking
db.users.drop()

// Then re-import
mongoimport --db salon_booking --collection users --file 1-users.json --jsonArray
```

### Issue 5: Appointments/Reviews import fails

**Problem:** Referenced documents don't exist

**Solution:**
- **Always follow insertion order:**
  1. Users
  2. Salons
  3. Customers
  4. Services
  5. Time Slots
  6. Appointments (references customers, salons, services, time_slots)
  7. Reviews (references salons, users, appointments)

### Issue 6: MongoDB Shell script shows errors

**Problem:** `use salon_booking;` shows syntax error in VS Code

**Solution:**
- This is **expected**. VS Code doesn't recognize MongoDB shell syntax.
- The script is correct and will run fine in `mongosh`.
- Ignore VS Code lint errors for `.js` MongoDB shell scripts.

### Issue 7: DataInitializer not running

**Problem:** Spring Boot doesn't trigger data initialization

**Possible Causes:**
1. Database not empty (check with Compass)
2. MongoDB not running (check with `mongosh`)
3. Wrong connection string in `application.properties`
4. Profile set to `prod` (DataInitializer has `@Profile("!prod")`)

**Solution:**
```powershell
# Check MongoDB is running
mongosh --version

# Test connection
mongosh mongodb://localhost:27017

# Check application.properties
# Ensure: spring.data.mongodb.uri=mongodb://localhost:27017/salon_booking

# Check profile (should be default or dev, NOT prod)
# In application.properties:
# spring.profiles.active=dev  # or leave empty
```

---

## Testing Scenarios

### Scenario 1: Book an Appointment

```javascript
// 1. Find available time slots for Salon 1
db.time_slots.find({ 
  "salon.$id": "salon1", 
  isAvailable: true 
}).limit(5)

// 2. Check salon services
db.services.find({}).limit(5)

// 3. Get customer
db.customers.findOne({ _id: "customer1" })

// 4. Create appointment (in application, this would update time slot)
```

### Scenario 2: Check Salon Reviews

```javascript
// Get salon with highest rating
db.salons.find().sort({ averageRating: -1 }).limit(1)

// Get reviews for that salon
db.reviews.find({ "salon.$id": "salon4" }).sort({ reviewDate: -1 })

// Calculate actual average (should match salon.averageRating)
db.reviews.aggregate([
  { $match: { "salon.$id": "salon4", isVisible: true } },
  { $group: { _id: null, avgRating: { $avg: "$rating" }, count: { $sum: 1 } } }
])
```

### Scenario 3: Customer Booking History

```javascript
// Find all appointments for a customer
db.appointments.find({ "customer.$id": "customer1" })

// Count by status
db.appointments.aggregate([
  { $match: { "customer.$id": "customer1" } },
  { $group: { _id: "$status", count: { $sum: 1 } } }
])
```

---

## Summary

### Quick Comparison

| Method | Speed | Ease of Use | Completeness | Best For |
|--------|-------|-------------|--------------|----------|
| **MongoDB Compass** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Beginners, manual control |
| **MongoDB Shell** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Developers, full automation |
| **DataInitializer** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Development, first runs |

### Recommended Workflow

**For Development:**
1. Use **Method 3 (DataInitializer)** for automatic setup (users, salons, customers, services, time slots)
2. Then import appointments/reviews via **Method 1 (Compass)** for full data

**For Production-like Testing:**
1. Use **Method 2 (MongoDB Shell)** for complete, reproducible setup
2. Script generates all 806 documents automatically

**For Learning/Exploration:**
1. Use **Method 1 (Compass)** to manually see each step
2. Understand relationships and data structure

---

## Next Steps

After inserting data:

1. **Test Spring Boot APIs:**
   - GET `/api/salons` - List all salons
   - GET `/api/services` - List all services
   - GET `/api/time-slots?salonId=salon1&date=2025-10-11` - Get available slots
   - POST `/api/appointments` - Create booking

2. **Test Frontend:**
   - Browse salons
   - View salon details and reviews
   - Book appointment
   - View customer booking history

3. **Explore MongoDB Compass:**
   - View relationships between collections
   - Test queries
   - Update documents manually
   - Create indexes for performance

4. **Learn MongoDB:**
   - Practice aggregation queries
   - Understand DBRef relationships
   - Optimize with indexes
   - Monitor performance

---

## Additional Resources

- **Documentation:** See `SAMPLE_DATA_STRUCTURE.md` for detailed explanations of all collections
- **Sample Files:** All in `backend/sample-data/` directory
- **MongoDB Docs:** https://docs.mongodb.com/
- **MongoDB Compass:** https://www.mongodb.com/products/compass
- **MongoDB Shell:** https://www.mongodb.com/docs/mongodb-shell/

---

## Support

If you encounter issues:

1. Check **Troubleshooting** section above
2. Review `SAMPLE_DATA_STRUCTURE.md` for data structure
3. Verify MongoDB is running: `mongosh`
4. Check Spring Boot logs for errors
5. Use MongoDB Compass to visually inspect data

**Happy coding! 🚀**
