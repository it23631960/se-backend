# ✅ Review Database Schema - Quick Reference (CS2-56)

## 🎯 Task Complete

Successfully created complete Review database schema for Spring Boot MongoDB application.

---

## 📦 Created Files

### 1. **Enhanced Model**
- ✅ `models/Review.java` - Complete review entity with 20+ fields

### 2. **DTOs** (4 files)
- ✅ `dto/CreateReviewDTO.java` - Create review request
- ✅ `dto/UpdateReviewDTO.java` - Update review request  
- ✅ `dto/ReviewResponseDTO.java` - Review response
- ✅ `dto/RatingStatisticsDTO.java` - Statistics & distribution

### 3. **Repository**
- ✅ `repositories/ReviewRepository.java` - Enhanced with 25+ query methods

### 4. **Model Enhancement**
- ✅ `models/Salon.java` - Added `averageRating` and `totalReviews` cache fields

### 5. **Documentation**
- ✅ `REVIEW_DATABASE_SCHEMA.md` - Complete specification (300+ lines)
- ✅ This quick reference file

---

## 🔑 Key Features

### Review Entity Fields:
```java
@Document(collection = "reviews")
public class Review {
    String id;                      // Auto-generated
    @DBRef Salon salon;             // Required - salon being reviewed
    @DBRef User user;               // Optional - null for guest reviews
    String reviewerName;            // Required (2-100 chars)
    String reviewerEmail;           // Required (valid email)
    Integer rating;                 // Required (1-5 stars)
    String comment;                 // Required (10-500 chars)
    LocalDateTime reviewDate;       // Auto-set
    LocalDateTime lastModified;     // For edited reviews
    Boolean isVerified;             // From actual appointment
    @DBRef Appointment appointment; // Link to verified booking
    Boolean isVisible;              // Moderation flag (default: true)
    Integer helpfulCount;           // "Helpful" votes (default: 0)
    Integer reportCount;            // Spam reports (default: 0)
    String moderatorNotes;          // Admin notes (internal)
    String ownerResponse;           // Salon owner response
    LocalDateTime ownerResponseDate;
}
```

### Indexes:
```java
// Compound indexes for performance
@CompoundIndexes({
    @CompoundIndex(def = "{'salon': 1, 'reviewDate': -1}"),  // Fast sorted queries
    @CompoundIndex(def = "{'salon': 1, 'user': 1}", unique = true)  // Prevent duplicates
})

// Single indexes
@Indexed private Salon salon;
@Indexed private User user;
@Indexed private LocalDateTime reviewDate;
@Indexed private Boolean isVisible;
```

### Validation Rules:
- ✅ Rating: 1-5 stars (required)
- ✅ Comment: 10-500 characters (required)
- ✅ Name: 2-100 characters (required)
- ✅ Email: Valid format (required)
- ✅ One review per user per salon (unique constraint)

---

## 📊 Repository Query Methods

### Essential Queries:
```java
// Get all visible reviews for salon (sorted by date)
List<Review> findBySalon_IdAndIsVisibleTrueOrderByReviewDateDesc(String salonId)

// Check if user already reviewed salon (prevent duplicates)
Boolean existsBySalon_IdAndUser_Id(String salonId, String userId)

// Get user's review for specific salon
Optional<Review> findBySalon_IdAndUser_Id(String salonId, String userId)

// Get user's all reviews
List<Review> findByUser_Id(String userId)

// Filter by rating
List<Review> findBySalon_IdAndRatingAndIsVisibleTrue(String salonId, Integer rating)

// Get verified reviews only
List<Review> findBySalon_IdAndIsVerifiedTrueAndIsVisibleTrue(String salonId)

// Count total reviews
Long countBySalon_IdAndIsVisibleTrue(String salonId)
```

### Statistics Queries:
```java
// Calculate average rating (aggregation)
Optional<RatingStatisticsDTO> calculateAverageRating(String salonId)

// Get full rating distribution (1-5 star breakdown)
Optional<RatingStatisticsDTO> getRatingDistribution(String salonId)
```

---

## 🎨 DTOs

### CreateReviewDTO (POST)
```java
{
  "salonId": "salon123",           // Required
  "userId": "user456",             // Optional (guest if null)
  "reviewerName": "Sarah Johnson", // Required
  "reviewerEmail": "sarah@example.com", // Required
  "rating": 5,                     // Required (1-5)
  "comment": "Amazing service...", // Required (10-500 chars)
  "appointmentId": "appt789"       // Optional (for verified)
}
```

### UpdateReviewDTO (PUT)
```java
{
  "rating": 4,                     // Required (1-5)
  "comment": "Updated review..."   // Required (10-500 chars)
}
```

### ReviewResponseDTO (GET)
```java
{
  "id": "review123",
  "salonId": "salon456",
  "salonName": "Glamour Salon",
  "userId": "user789",
  "reviewerName": "Sarah Johnson",
  "rating": 5,
  "comment": "Amazing service!",
  "reviewDate": "2025-10-08T10:30:00",
  "lastModified": null,
  "isVerified": true,
  "helpfulCount": 12,
  "isGuestReview": false,
  "isEditable": false,             // Only within 24 hours
  "ownerResponse": "Thank you!",
  "ownerResponseDate": "2025-10-09T14:20:00"
}
```

### RatingStatisticsDTO (GET stats)
```java
{
  "salonId": "salon123",
  "averageRating": 4.8,
  "totalReviews": 124,
  "fiveStars": 95,
  "fourStars": 20,
  "threeStars": 5,
  "twoStars": 3,
  "oneStar": 1
}
```

---

## 💡 Business Rules

### Review Submission:
1. ✅ **One review per user per salon** (enforced by unique index)
2. ✅ **Guest reviews allowed** (user = null)
3. ✅ **Rating required** (1-5 stars)
4. ✅ **Comment minimum 10 characters** (quality control)
5. ✅ **Auto-populate user info** if logged in

### Review Editing:
1. ✅ **Editable within 24 hours** of creation
2. ✅ **Only rating & comment** can be changed
3. ✅ **lastModified timestamp** updated automatically

### Verification:
1. ✅ **Link to appointment** → marks as verified
2. ✅ **One review per appointment** (prevent duplicates)
3. ✅ **Display special badge** for verified reviews

### Moderation:
1. ✅ **isVisible flag** for hiding inappropriate reviews
2. ✅ **reportCount** for spam detection
3. ✅ **Auto-hide** if reports exceed threshold
4. ✅ **moderatorNotes** for internal tracking

---

## 🚀 Performance Optimizations

### 1. Salon Rating Cache
```java
// Added to Salon model
private Double averageRating;  // Cached from reviews
private Long totalReviews;     // Cached count

// Update strategy:
// - Update asynchronously when review is added/modified
// - Avoids expensive aggregation queries on every salon list
```

### 2. Database Indexes
```java
// Compound index optimizes common queries
{'salon': 1, 'reviewDate': -1}  → Fast sorted queries
{'salon': 1, 'user': 1}         → Prevents duplicates + fast lookups
```

### 3. Projection Queries
```java
// Fetch only rating field (not full document)
@Query(fields = "{ 'rating': 1 }")
List<Review> findRatingsBySalonId(String salonId)
```

---

## 📋 Sample Data Structure

### MongoDB Document:
```json
{
  "_id": "review123",
  "salon": { "$ref": "salons", "$id": "salon456" },
  "user": { "$ref": "users", "$id": "user789" },
  "reviewerName": "Sarah Johnson",
  "reviewerEmail": "sarah@example.com",
  "rating": 5,
  "comment": "Amazing service! Highly recommend.",
  "reviewDate": "2025-10-08T10:30:00",
  "lastModified": null,
  "isVerified": true,
  "appointment": { "$ref": "appointments", "$id": "appt101" },
  "isVisible": true,
  "helpfulCount": 12,
  "reportCount": 0,
  "moderatorNotes": null,
  "ownerResponse": "Thank you for your kind words!",
  "ownerResponseDate": "2025-10-09T14:20:00"
}
```

---

## 🛠️ Next Steps (Implementation)

### Service Layer:
- [ ] Create `ReviewService` with business logic
- [ ] Implement duplicate check before creation
- [ ] Add rating cache update method
- [ ] Implement 24-hour edit window check

### Controller Layer:
- [ ] Create `ReviewController` with REST endpoints
- [ ] Add request/response mapping
- [ ] Implement validation error handling
- [ ] Add security (user ownership verification)

### Testing:
- [ ] Unit tests for repository queries
- [ ] Integration tests for service layer
- [ ] Controller endpoint tests
- [ ] Create sample data seeder

### Admin Features:
- [ ] Moderation endpoints (hide/show)
- [ ] Reported reviews dashboard
- [ ] Moderator notes management
- [ ] Owner response functionality

---

## 📚 API Endpoints (Recommended)

```
POST   /api/reviews                           - Create new review
GET    /api/reviews/{id}                      - Get single review
PUT    /api/reviews/{id}                      - Update review
DELETE /api/reviews/{id}                      - Delete review

GET    /api/reviews/salon/{salonId}           - Get all salon reviews
GET    /api/reviews/salon/{salonId}/statistics - Get rating statistics
GET    /api/reviews/user/{userId}             - Get user's reviews

POST   /api/reviews/{id}/helpful              - Mark review helpful
POST   /api/reviews/{id}/report               - Report spam

// Admin endpoints
GET    /api/admin/reviews/reported            - Get reported reviews
PUT    /api/admin/reviews/{id}/visibility     - Toggle visibility
```

---

## ✅ Deliverables Summary

| Component | Status | File Location |
|-----------|--------|---------------|
| Review Entity | ✅ Complete | `models/Review.java` |
| Review Repository | ✅ Complete | `repositories/ReviewRepository.java` |
| Create DTO | ✅ Complete | `dto/CreateReviewDTO.java` |
| Update DTO | ✅ Complete | `dto/UpdateReviewDTO.java` |
| Response DTO | ✅ Complete | `dto/ReviewResponseDTO.java` |
| Statistics DTO | ✅ Complete | `dto/RatingStatisticsDTO.java` |
| Salon Enhancement | ✅ Complete | `models/Salon.java` |
| Documentation | ✅ Complete | `REVIEW_DATABASE_SCHEMA.md` |
| Quick Reference | ✅ Complete | `REVIEW_SCHEMA_QUICK_REF.md` |

---

## 🎉 All Database Schema Components Ready!

**Frontend integration:** Review components already created and styled with glassmorphism design at `frontend/src/components/reviews/`

**Backend next step:** Create `ReviewService` and `ReviewController` to expose REST API

**Documentation:** See `REVIEW_DATABASE_SCHEMA.md` for complete specification

---

**Task:** CS2-56 - Create Review Database Schema  
**Status:** ✅ Complete  
**Date:** October 10, 2025
