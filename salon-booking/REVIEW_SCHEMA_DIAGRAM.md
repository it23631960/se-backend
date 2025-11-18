# Review Database Schema - Visual Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         REVIEW ENTITY                                │
│                    Collection: "reviews"                             │
└─────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────────┐
│  CORE FIELDS                                                          │
├───────────────────────────────────────────────────────────────────────┤
│  📌 id                    : String (Auto-generated)                   │
│  🏢 salon                 : @DBRef → Salon (Required, Indexed)       │
│  👤 user                  : @DBRef → User (Optional, Indexed)         │
│  ✍️  reviewerName          : String (Required, 2-100 chars)          │
│  📧 reviewerEmail         : String (Required, Valid email)            │
│  ⭐ rating                : Integer (Required, 1-5)                   │
│  💬 comment               : String (Required, 10-500 chars)          │
│  📅 reviewDate            : LocalDateTime (Auto-set, Indexed)        │
└───────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────────┐
│  OPTIONAL FIELDS                                                      │
├───────────────────────────────────────────────────────────────────────┤
│  🕒 lastModified          : LocalDateTime (When edited)               │
│  ✅ isVerified            : Boolean (Default: false)                 │
│  📋 appointment           : @DBRef → Appointment (For verified)       │
│  👁️  isVisible             : Boolean (Default: true, Indexed)         │
│  👍 helpfulCount          : Integer (Default: 0)                     │
│  🚩 reportCount           : Integer (Default: 0)                     │
│  📝 moderatorNotes        : String (Admin only)                      │
│  💼 ownerResponse         : String (Salon owner reply)               │
│  📅 ownerResponseDate     : LocalDateTime (When owner replied)       │
└───────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────┐
│                         DATABASE INDEXES                             │
└─────────────────────────────────────────────────────────────────────┘

1️⃣  COMPOUND INDEX: salon_date_idx
    ┌──────────────────────────────────────────┐
    │  { salon: 1, reviewDate: -1 }           │
    │  Purpose: Fast sorted queries per salon  │
    │  Use: Get recent reviews for salon      │
    └──────────────────────────────────────────┘

2️⃣  COMPOUND INDEX: salon_user_unique_idx (UNIQUE, SPARSE)
    ┌──────────────────────────────────────────┐
    │  { salon: 1, user: 1 }                  │
    │  Purpose: Prevent duplicate reviews      │
    │  Use: One review per user per salon     │
    └──────────────────────────────────────────┘

3️⃣  SINGLE INDEXES:
    • salon      → Fast salon lookups
    • user       → User's review history
    • reviewDate → Date-based sorting
    • isVisible  → Filter visible reviews


┌─────────────────────────────────────────────────────────────────────┐
│                      RELATIONSHIPS DIAGRAM                           │
└─────────────────────────────────────────────────────────────────────┘

    ┌──────────┐
    │  User    │ (Optional)
    │  ────────│
    │  id      │
    │  username│────────┐
    │  email   │        │
    └──────────┘        │
                        │ @DBRef (nullable)
                        │
    ┌──────────┐        │     ┌──────────────┐
    │  Salon   │        │     │   Review     │
    │  ────────│        └────→│  ──────────  │
    │  id      │←────────────→│  id          │
    │  name    │   @DBRef     │  salon       │
    │  rating* │   (required) │  user        │
    │  total*  │              │  rating      │
    └──────────┘              │  comment     │
       ↑                      │  ...         │
       │                      └──────────────┘
       │                            │
       │                            │ @DBRef (optional)
       │                            │
       │                      ┌──────────────┐
       │                      │ Appointment  │
       │                      │  ──────────  │
       └──────────────────────│  id          │
         Updates cache        │  customer    │
         asynchronously       │  service     │
                             │  salon       │
                             └──────────────┘

* = Cached fields (averageRating, totalReviews)


┌─────────────────────────────────────────────────────────────────────┐
│                      BUSINESS RULES FLOW                             │
└─────────────────────────────────────────────────────────────────────┘

CREATE REVIEW:
   1. Validate inputs (rating 1-5, comment 10-500 chars)
   2. Check if user already reviewed salon
      └─→ If exists: Return error "Already reviewed"
   3. If appointmentId provided:
      └─→ Check appointment exists
      └─→ Set isVerified = true
   4. Save review
   5. Update salon cache (async)
      └─→ Calculate new averageRating
      └─→ Increment totalReviews

UPDATE REVIEW:
   1. Check if review exists
   2. Check if user owns review
   3. Check if within 24 hours (isEditable)
      └─→ If expired: Return error "Cannot edit"
   4. Update rating and comment
   5. Set lastModified = now()
   6. Update salon cache (async)

DELETE REVIEW:
   1. Check if user owns review
   2. Soft delete (set isVisible = false)
      OR Hard delete (remove document)
   3. Update salon cache (async)
      └─→ Recalculate averageRating
      └─→ Decrement totalReviews


┌─────────────────────────────────────────────────────────────────────┐
│                      QUERY PATTERNS                                  │
└─────────────────────────────────────────────────────────────────────┘

📊 GET SALON REVIEWS:
   Query: findBySalon_IdAndIsVisibleTrueOrderByReviewDateDesc(salonId)
   Index Used: salon_date_idx (compound)
   Performance: O(log n) + O(k) where k = result count

🔍 CHECK DUPLICATE:
   Query: existsBySalon_IdAndUser_Id(salonId, userId)
   Index Used: salon_user_unique_idx (compound)
   Performance: O(log n)

📈 GET STATISTICS:
   Query: getRatingDistribution(salonId)
   Method: MongoDB Aggregation Pipeline
   Performance: O(n) where n = reviews for salon
   Result: { avgRating, total, fiveStars, fourStars, ... }

🎯 FILTER BY RATING:
   Query: findBySalon_IdAndRatingAndIsVisibleTrue(salonId, 5)
   Index Used: salon (partial)
   Performance: O(log n) + O(k)


┌─────────────────────────────────────────────────────────────────────┐
│                      CACHING STRATEGY                                │
└─────────────────────────────────────────────────────────────────────┘

❌ WITHOUT CACHE (Expensive):
   ┌────────────┐
   │ Get Salon  │
   │   List     │
   └─────┬──────┘
         │
         ├─→ For each salon: Run aggregation query
         │   ├─→ Calculate AVG(rating)
         │   └─→ Count reviews
         │
   Cost: N queries for N salons

✅ WITH CACHE (Fast):
   ┌────────────┐
   │ Get Salon  │
   │   List     │
   └─────┬──────┘
         │
         └─→ Read averageRating & totalReviews
             from Salon document
   
   Cost: 0 extra queries!
   
   Update Strategy:
   ┌──────────────┐
   │ Review CRUD  │
   └──────┬───────┘
          │
          └─→ Async: Update Salon cache
              ├─→ Aggregate all reviews
              └─→ Update salon.averageRating
                  & salon.totalReviews


┌─────────────────────────────────────────────────────────────────────┐
│                      SAMPLE JSON DOCUMENT                            │
└─────────────────────────────────────────────────────────────────────┘

{
  "_id": "671d8f9a1234567890abcdef",
  
  // Core fields
  "salon": {
    "$ref": "salons",
    "$id": ObjectId("671d8f9a1234567890abc123")
  },
  "user": {
    "$ref": "users",
    "$id": ObjectId("671d8f9a1234567890abc456")
  },
  "reviewerName": "Sarah Johnson",
  "reviewerEmail": "sarah@example.com",
  "rating": 5,
  "comment": "Amazing service! The staff was incredibly professional...",
  "reviewDate": ISODate("2025-10-08T10:30:00.000Z"),
  
  // Optional fields
  "lastModified": null,
  "isVerified": true,
  "appointment": {
    "$ref": "appointments",
    "$id": ObjectId("671d8f9a1234567890abc789")
  },
  "isVisible": true,
  "helpfulCount": 12,
  "reportCount": 0,
  "moderatorNotes": null,
  "ownerResponse": "Thank you for your kind words!",
  "ownerResponseDate": ISODate("2025-10-09T14:20:00.000Z")
}


┌─────────────────────────────────────────────────────────────────────┐
│                      API ENDPOINTS MAP                               │
└─────────────────────────────────────────────────────────────────────┘

Public Endpoints:
   POST   /api/reviews                    ← CreateReviewDTO
   GET    /api/reviews/{id}               → ReviewResponseDTO
   PUT    /api/reviews/{id}               ← UpdateReviewDTO
   DELETE /api/reviews/{id}               → 204 No Content
   
   GET    /api/reviews/salon/{salonId}    → List<ReviewResponseDTO>
   GET    /api/reviews/salon/{salonId}/statistics → RatingStatisticsDTO
   GET    /api/reviews/user/{userId}      → List<ReviewResponseDTO>
   
   POST   /api/reviews/{id}/helpful       → ReviewResponseDTO
   POST   /api/reviews/{id}/report        → ReviewResponseDTO

Admin Endpoints:
   GET    /api/admin/reviews/reported     → List<ReviewResponseDTO>
   PUT    /api/admin/reviews/{id}/visibility → ReviewResponseDTO
   PUT    /api/admin/reviews/{id}/notes   ← moderatorNotes


┌─────────────────────────────────────────────────────────────────────┐
│                      DATA FLOW EXAMPLE                               │
└─────────────────────────────────────────────────────────────────────┘

User Submits Review:

   [Frontend]                [Backend]              [Database]
       │                         │                       │
       │ POST /api/reviews       │                       │
       │ CreateReviewDTO         │                       │
       ├────────────────────────→│                       │
       │                         │                       │
       │                         │ 1. Validate DTO       │
       │                         │ 2. Check duplicate    │
       │                         ├──────────────────────→│
       │                         │←──────────────────────┤
       │                         │   exists?             │
       │                         │                       │
       │                         │ 3. Save Review        │
       │                         ├──────────────────────→│
       │                         │←──────────────────────┤
       │                         │   Review saved        │
       │                         │                       │
       │                         │ 4. Update Salon cache │
       │                         ├──────────────────────→│
       │                         │   (async)             │
       │                         │                       │
       │←────────────────────────┤                       │
       │ ReviewResponseDTO       │                       │


┌─────────────────────────────────────────────────────────────────────┐
│                      VALIDATION SUMMARY                              │
└─────────────────────────────────────────────────────────────────────┘

Field                 Validation
────────────────────  ─────────────────────────────────────────────
salon                 @NotNull, @DBRef must exist
user                  Optional (null for guests)
reviewerName          @NotBlank, @Size(min=2, max=100)
reviewerEmail         @NotBlank, @Email
rating                @NotNull, @Min(1), @Max(5)
comment               @NotBlank, @Size(min=10, max=500)
reviewDate            Auto-set to LocalDateTime.now()
isVerified            Default: false
isVisible             Default: true
helpfulCount          Default: 0
reportCount           Default: 0

Business Logic:
✅ One review per (user + salon) combination
✅ Can only edit within 24 hours
✅ Cannot change salon or reviewer info after creation
✅ Only rating and comment can be updated


┌─────────────────────────────────────────────────────────────────────┐
│                      FILES CREATED                                   │
└─────────────────────────────────────────────────────────────────────┘

📁 backend/salon-booking/src/main/java/com/example/salon_booking/

   models/
   └─ ✅ Review.java (Enhanced - 245 lines)
   
   repositories/
   └─ ✅ ReviewRepository.java (Enhanced - 200+ lines)
   
   dto/
   ├─ ✅ CreateReviewDTO.java (New)
   ├─ ✅ UpdateReviewDTO.java (New)
   ├─ ✅ ReviewResponseDTO.java (New)
   └─ ✅ RatingStatisticsDTO.java (New)

📄 Documentation:
   ├─ ✅ REVIEW_DATABASE_SCHEMA.md (Complete spec - 500+ lines)
   ├─ ✅ REVIEW_SCHEMA_QUICK_REF.md (Quick reference)
   └─ ✅ REVIEW_SCHEMA_DIAGRAM.md (This file - Visual diagrams)


┌─────────────────────────────────────────────────────────────────────┐
│                      NEXT STEPS                                      │
└─────────────────────────────────────────────────────────────────────┘

1️⃣  Create ReviewService
    └─ Implement business logic
    └─ Add duplicate check
    └─ Add cache update method

2️⃣  Create ReviewController
    └─ Map REST endpoints
    └─ Add validation
    └─ Add security checks

3️⃣  Write Tests
    └─ Repository tests
    └─ Service tests
    └─ Controller tests

4️⃣  Connect Frontend
    └─ Frontend review components exist at:
        frontend/src/components/reviews/
    └─ Update API calls to backend

5️⃣  Add Sample Data
    └─ Create data seeder
    └─ Generate test reviews


✅ DATABASE SCHEMA COMPLETE!
```
