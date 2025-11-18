# 📦 Sample Data Package for Salon Booking System

## 🎯 Overview

This package contains **comprehensive sample data** for the Salon Booking MongoDB database, including:

- **806 total documents** across 7 collections
- **Realistic Sri Lankan data** (cities, names, phone numbers, prices in LKR)
- **Complete relationships** with proper @DBRef structure
- **Production-quality content** (no placeholder text, realistic reviews)
- **Three insertion methods** (MongoDB Compass, MongoDB Shell, Spring Boot)

---

## 📁 Package Contents

### Documentation Files (2)

| File | Description | Size |
|------|-------------|------|
| `SAMPLE_DATA_STRUCTURE.md` | 40+ page comprehensive guide explaining all collections, fields, relationships | 📚 Large |
| `DATA_INSERTION_GUIDE.md` | Step-by-step insertion instructions for all 3 methods | 📖 Medium |

### Data Files (7 JSON + 1 Script)

Located in `backend/sample-data/`:

| # | File | Records | Description |
|---|------|---------|-------------|
| 1 | `1-users.json` | 10 | User accounts for authentication & reviews |
| 2 | `2-salons.json` | 6 | Salon businesses across Sri Lanka |
| 3 | `3-services.json` | 30 | Services (haircut, spa, bridal, nails) |
| 4 | `4-customers.json` | 15 | Customer profiles (registered + guest) |
| 5 | `5-timeslots-sample.json` | 16 | Sample time slots (full 672 via script) |
| 6 | `6-appointments.json` | 25 | Appointments (all statuses) |
| 7 | `7-reviews.json` | 48 | Detailed reviews with ratings |
| 📜 | `mongo-insert-script.js` | - | MongoDB Shell script (generates 672 slots) |

### Spring Boot Code (1)

| File | Description |
|------|-------------|
| `DataInitializer.java` | Auto-seeds database on startup (generates 672 slots) |

---

## 📊 Data Summary

### Collection Breakdown

```
Database: salon_booking
├── users          (10 docs)   ✅ Authentication & reviews
├── salons         (6 docs)    ✅ Businesses in 6 cities
├── services       (30 docs)   ✅ Services with LKR pricing
├── customers      (15 docs)   ✅ Registered + guest customers
├── time_slots     (672 docs)  ✅ 7 days of availability
├── appointments   (25 docs)   ✅ All statuses represented
└── reviews        (48 docs)   ✅ Realistic comments & ratings

Total: 806 documents
```

### Geographic Distribution

**6 Salons across Sri Lanka:**

1. **Elite Hair Studio** - Colombo (4.7⭐, 12 reviews)
2. **Golden Locks Salon** - Kandy (4.5⭐, 8 reviews)
3. **Style Avenue Spa** - Galle (4.8⭐, 10 reviews)
4. **Royal Bridal Salon** - Negombo (4.9⭐, 6 reviews)
5. **Trendy Cuts Unisex** - Jaffna (4.3⭐, 7 reviews)
6. **Ocean Breeze Salon** - Matara (4.6⭐, 5 reviews)

### Service Categories

- **HAIRCUT** (6 services): Men's, Women's, Kids, Express, Beard Trim
- **COLORING** (3 services): Full Color, Highlights, Root Touch-Up
- **STYLING** (4 services): Blowdry, Extensions, Braiding, Beach Waves
- **SPA** (5 services): Facial, Massage, Waxing, Hair Spa
- **NAILS** (3 services): Manicure, Pedicure, Gel Nails
- **BRIDAL** (5 services): Bridal Package, Trial, Party Makeup, Saree Draping, Henna
- **TREATMENT** (3 services): Keratin, Straightening, Scalp Treatment
- **THREADING** (1 service): Eyebrows & Upper Lip

**Price Range:** Rs. 500 (threading) to Rs. 50,000 (bridal package)

### Appointment Distribution

- **12 CONFIRMED** - Upcoming appointments (Oct 11-17)
- **5 COMPLETED** - Past appointments (Sept-Oct)
- **3 PENDING** - Awaiting confirmation
- **3 CANCELLED** - With cancellation reasons
- **2 NO_SHOW** - Past no-shows

### Review Distribution

- **5 stars:** 29 reviews (60%)
- **4 stars:** 12 reviews (25%)
- **3 stars:** 5 reviews (10%)
- **1-2 stars:** 2 reviews (5%)

All reviews include detailed comments (100-200 words), owner responses, helpful counts, and verified status.

---

## 🚀 Quick Start

### Choose Your Method

#### **Option 1: MongoDB Compass (GUI)** - Easiest

```
1. Open MongoDB Compass
2. Connect to mongodb://localhost:27017
3. Create database: salon_booking
4. Import JSON files in order (1→7)
5. Done! ✅
```

**Time:** ~10 minutes  
**Skill Level:** Beginner  
**See:** `DATA_INSERTION_GUIDE.md` - Method 1

---

#### **Option 2: MongoDB Shell (CLI)** - Fastest

```powershell
# Navigate to sample-data directory
cd "e:\Saloon\Hasith\backend\sample-data"

# Run script in mongosh
mongosh
> load('mongo-insert-script.js')

# Import appointments and reviews
mongoimport --db salon_booking --collection appointments --file 6-appointments.json --jsonArray
mongoimport --db salon_booking --collection reviews --file 7-reviews.json --jsonArray
```

**Time:** ~2 minutes  
**Skill Level:** Intermediate  
**See:** `DATA_INSERTION_GUIDE.md` - Method 2

---

#### **Option 3: Spring Boot (Automatic)** - Best for Dev

```powershell
# Just run your Spring Boot app
.\mvnw spring-boot:run

# DataInitializer auto-seeds database on first run
# Inserts users, salons, customers, services, time slots
# Then import appointments/reviews via Compass or Shell
```

**Time:** ~1 minute (automatic)  
**Skill Level:** Beginner  
**See:** `DATA_INSERTION_GUIDE.md` - Method 3

---

## 📖 Documentation

### For Understanding Data Structure

👉 **Read:** `SAMPLE_DATA_STRUCTURE.md` (40+ pages)

**What's Inside:**
- Complete explanation of all 7 collections
- Field-by-field documentation with types, validation, sample values
- Relationship mappings and @DBRef structure
- Insertion order and dependencies
- ID mapping reference (salon1-6, user1-10, etc.)
- Critical relationship rules
- Sri Lankan context guidelines
- Testing scenarios
- Verification queries

**When to Read:**
- Before inserting data (understand structure)
- When designing APIs (understand relationships)
- When debugging issues (verify data integrity)
- When adding new features (understand existing data)

---

### For Inserting Data

👉 **Read:** `DATA_INSERTION_GUIDE.md`

**What's Inside:**
- Step-by-step instructions for all 3 methods
- Prerequisites for each method
- Verification queries
- Troubleshooting common issues
- Testing scenarios
- Quick comparison table

**When to Read:**
- Before inserting data (choose method)
- When encountering errors (troubleshooting)
- When testing application (verification)

---

## ⚙️ Technical Details

### Data Relationships

```
User
  └── Review (optional @DBRef)

Salon
  ├── TimeSlot (@DBRef required)
  ├── Review (@DBRef required)
  └── Appointment (@DBRef required)

Customer
  └── Appointment (@DBRef required)

Service
  └── Appointment (@DBRef required)

TimeSlot
  ├── Salon (@DBRef required)
  └── Appointment (@DBRef required)

Appointment (@DBRef to Customer, Service, TimeSlot, Salon)
  └── Review (optional @DBRef)
```

### Data Formats

- **Dates:** ISO 8601 (`YYYY-MM-DD` for LocalDate)
- **Times:** 24-hour format (`HH:MM` for LocalTime)
- **Timestamps:** ISO 8601 full (`YYYY-MM-DDTHH:MM:SS` for LocalDateTime)
- **Phone:** Sri Lankan international format (`+94XXXXXXXXX`)
- **Currency:** Sri Lankan Rupees (LKR)
- **DBRef:** `{ "$ref": "collection_name", "$id": "document_id" }`

### Insertion Order (Important!)

**Always insert in this order** (dependencies):

1. **Users** (no dependencies)
2. **Salons** (no dependencies)
3. **Customers** (no dependencies)
4. **Services** (no dependencies)
5. **TimeSlots** (depends on Salons)
6. **Appointments** (depends on Customers, Services, TimeSlots, Salons)
7. **Reviews** (depends on Salons, optionally Users & Appointments)

---

## 🔍 Verification

### Quick Health Check

Run in MongoDB Compass (MongoSH tab):

```javascript
use salon_booking

// Count all documents
db.users.countDocuments()         // Expected: 10
db.salons.countDocuments()        // Expected: 6
db.services.countDocuments()      // Expected: 30
db.customers.countDocuments()     // Expected: 15
db.time_slots.countDocuments()    // Expected: 672 (or 16 sample)
db.appointments.countDocuments()  // Expected: 25
db.reviews.countDocuments()       // Expected: 48

// Check sample data
db.salons.findOne({ _id: "salon1" })
db.appointments.findOne({ status: "CONFIRMED" })
```

### Deep Verification

See `DATA_INSERTION_GUIDE.md` → Verification Queries section for:
- Data quality checks
- Integrity validation
- Relationship verification
- Rating calculation checks

---

## 🛠️ Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| "Database already has data" | Drop collections and re-run |
| Time slots only 16 (not 672) | Use script or DataInitializer, not sample JSON |
| DBRef not resolving | Use repository methods (Spring resolves automatically) |
| Import fails with duplicate key | Drop collection and re-import |
| DataInitializer not running | Check MongoDB connection and profile |

**Full Troubleshooting Guide:** See `DATA_INSERTION_GUIDE.md` → Troubleshooting section

---

## 📦 File Locations

```
backend/
├── SAMPLE_DATA_STRUCTURE.md         ← Read this first (structure)
├── DATA_INSERTION_GUIDE.md          ← Read this second (instructions)
├── README_SAMPLE_DATA.md            ← You are here! (overview)
└── sample-data/
    ├── 1-users.json
    ├── 2-salons.json
    ├── 3-services.json
    ├── 4-customers.json
    ├── 5-timeslots-sample.json
    ├── 6-appointments.json
    ├── 7-reviews.json
    └── mongo-insert-script.js

backend/salon-booking/src/main/java/com/example/salon_booking/config/
└── DataInitializer.java             ← Spring Boot auto-seeder
```

---

## 🎓 Learning Path

### Beginner Path

1. **Read:** This README (overview)
2. **Read:** `DATA_INSERTION_GUIDE.md` → Method 1 (MongoDB Compass)
3. **Do:** Insert data using Compass (GUI)
4. **Explore:** Browse data in Compass
5. **Read:** `SAMPLE_DATA_STRUCTURE.md` (when needed)

### Intermediate Path

1. **Read:** This README + `SAMPLE_DATA_STRUCTURE.md` (skim)
2. **Do:** Insert data using MongoDB Shell (Method 2)
3. **Test:** Run verification queries
4. **Build:** Test Spring Boot APIs with sample data

### Advanced Path

1. **Read:** All documentation
2. **Do:** Use DataInitializer (Method 3)
3. **Customize:** Modify DataInitializer for your needs
4. **Extend:** Add more sample data or scenarios
5. **Optimize:** Create indexes, analyze performance

---

## 📊 Data Quality

### ✅ What Makes This Data Production-Quality

- **No placeholder text:** All descriptions are realistic and detailed
- **Realistic reviews:** 100-200 word comments with specific feedback
- **Proper relationships:** All @DBRef correctly structured
- **Date consistency:** Review dates align with appointment dates
- **Rating accuracy:** Salon averageRating matches actual review averages
- **Status distribution:** Appointments have realistic status mix
- **Sri Lankan context:** Names, cities, phone format, prices all localized
- **Professional content:** Service descriptions, salon details all professional
- **Variety:** Mix of registered/guest customers, verified/guest reviews
- **Completeness:** All required fields populated, optional fields realistic

---

## 🔄 Next Steps After Insertion

### 1. Test Spring Boot APIs

```java
// GET /api/salons - List all salons
// GET /api/services - List all services
// GET /api/time-slots?salonId=salon1&date=2025-10-11
// POST /api/appointments - Create booking
```

### 2. Test Frontend

- Browse salons (should see 6 salons)
- View salon details (should see reviews, ratings)
- Book appointment (should see available time slots)
- View customer history

### 3. Explore Relationships

```javascript
// In MongoDB Compass
db.appointments.findOne({}, { customer: 1, service: 1, timeSlot: 1, salon: 1 })
// See how @DBRef connects documents
```

### 4. Add More Data

- Modify `DataInitializer.java` to add more salons
- Create custom JSON files for your scenarios
- Extend `mongo-insert-script.js` for automation

---

## 💡 Tips

### For Development

- Use **Method 3** (DataInitializer) for automatic setup
- Import appointments/reviews via Compass for full data
- Drop database between tests: `db.dropDatabase()`

### For Testing

- Use **Method 2** (MongoDB Shell) for reproducible setup
- Script generates consistent data every time
- Verify with queries in `DATA_INSERTION_GUIDE.md`

### For Learning

- Use **Method 1** (Compass) to see each step
- Explore data visually
- Read `SAMPLE_DATA_STRUCTURE.md` alongside

---

## 🎯 Key Features

- ✅ **806 documents** across 7 collections
- ✅ **Realistic Sri Lankan data** (cities, names, phone, prices)
- ✅ **Production-quality content** (no placeholders)
- ✅ **Complete relationships** (proper @DBRef)
- ✅ **Three insertion methods** (GUI, CLI, Spring Boot)
- ✅ **Comprehensive documentation** (40+ pages)
- ✅ **Verification queries** (ensure data integrity)
- ✅ **Troubleshooting guide** (solve common issues)
- ✅ **Testing scenarios** (real-world use cases)
- ✅ **Programmatic time slot generation** (672 slots via code)

---

## 📞 Support

If you need help:

1. **Check Troubleshooting:** `DATA_INSERTION_GUIDE.md` → Troubleshooting
2. **Verify MongoDB Running:** `mongosh`
3. **Check Spring Boot Logs:** Look for DataInitializer output
4. **Inspect Data Visually:** Use MongoDB Compass
5. **Review Relationships:** See `SAMPLE_DATA_STRUCTURE.md`

---

## 🚀 Ready to Start?

1. **Choose your method** (see Quick Start above)
2. **Read the guide:** `DATA_INSERTION_GUIDE.md`
3. **Insert data** (follows step-by-step instructions)
4. **Verify data** (run verification queries)
5. **Start coding!** 🎉

---

**Happy Coding! 🚀**

---

## 📝 Version History

- **v1.0** (2025-10-10): Initial release
  - 806 documents across 7 collections
  - 3 insertion methods
  - Comprehensive documentation
  - Production-quality data
  - Sri Lankan localization

---

## 📄 License

This sample data is provided for educational and development purposes.

---

**For detailed information, see:**
- `SAMPLE_DATA_STRUCTURE.md` - Complete data structure guide
- `DATA_INSERTION_GUIDE.md` - Step-by-step insertion instructions
