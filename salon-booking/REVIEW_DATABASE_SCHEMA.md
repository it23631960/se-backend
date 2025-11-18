# Review Database Schema Documentation

## 📋 Overview

Complete database schema implementation for the Salon Booking System's review and rating functionality (Task CS2-56).

**Technology Stack:**
- Spring Boot 3.3.5
- MongoDB (Document Database)
- Lombok (Code Generation)
- Jakarta Validation

---

## 🗄️ Database Schema

### 1. Review Entity (`models/Review.java`)

**Collection:** `reviews`

**Description:** Stores customer reviews and ratings for salons. Supports both registered users and guest reviewers.

#### Core Fields:

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `id` | String | Yes (Auto) | - | Unique identifier |
| `salon` | Salon (@DBRef) | Yes | @NotNull | Reference to salon being reviewed |
| `user` | User (@DBRef) | No | - | Reference to registered user (null for guests) |
| `reviewerName` | String | Yes | 2-100 chars | Name of person writing review |
| `reviewerEmail` | String | Yes | Valid email | Email of reviewer |
| `rating` | Integer | Yes | 1-5 | Star rating |
| `comment` | String | Yes | 10-500 chars | Review text |
| `reviewDate` | LocalDateTime | Yes (Auto) | - | When review was created |

#### Optional/Moderation Fields:

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `lastModified` | LocalDateTime | null | When review was last edited |
| `isVerified` | Boolean | false | Review from actual appointment |
| `appointment` | Appointment (@DBRef) | null | Link to verified appointment |
| `isVisible` | Boolean | true | Public visibility (moderation) |
| `helpfulCount` | Integer | 0 | Number of "helpful" votes |
| `reportCount` | Integer | 0 | Spam reports count |
| `moderatorNotes` | String | null | Admin notes (internal) |
| `ownerResponse` | String | null | Salon owner's response |
| `ownerResponseDate` | LocalDateTime | null | When owner responded |

#### Business Logic Methods:

```java
public boolean isEditable()        // Can edit within 24 hours
public boolean isGuestReview()     // True if no user link
public String getDisplayName()     // Get reviewer name or "Anonymous"
public void incrementHelpfulCount()
public void incrementReportCount()
```

---

### 2. MongoDB Indexes

**Purpose:** Optimize query performance and enforce business rules

#### Defined Indexes:

```java
@CompoundIndexes({
    // Fast sorted queries per salon
    @CompoundIndex(
        name = "salon_date_idx", 
        def = "{'salon': 1, 'reviewDate': -1}"
    ),
    
    // Prevent duplicate reviews (one per user per salon)
    @CompoundIndex(
        name = "salon_user_unique_idx", 
        def = "{'salon': 1, 'user': 1}", 
        unique = true, 
        sparse = true
    )
})

// Single field indexes
@Indexed private Salon salon;        // Fast salon lookups
@Indexed private User user;          // User's review history
@Indexed private LocalDateTime reviewDate;  // Date sorting
@Indexed private Boolean isVisible;  // Filter visible reviews
```

**Performance Impact:**
- ✅ Fast queries: `findBySalonId` uses salon index
- ✅ Sorted results: Compound index optimizes `ORDER BY reviewDate`
- ✅ Duplicate prevention: Unique constraint at database level
- ✅ Moderation: `isVisible` index for filtering

---

### 3. Salon Model Enhancement

**Collection:** `salons`

Added cached rating fields for performance:

```java
@Document(collection = "salons")
public class Salon {
    // ... existing fields ...
    
    /**
     * Cached average rating (0.0 to 5.0)
     * Updated automatically when reviews change
     * Avoids expensive aggregation queries
     */
    private Double averageRating;
    
    /**
     * Cached total review count
     * Updated automatically when reviews added/deleted
     */
    private Long totalReviews = 0L;
}
```

**Benefits:**
- ⚡ **Fast Reads:** No aggregation needed for salon listings
- 📊 **Statistics:** Ready-to-use rating display
- 🔄 **Updated Async:** Service layer updates cache when reviews change

---

## 🔍 Repository Interface (`ReviewRepository.java`)

### Query Methods by Category:

#### Basic Queries
```java
// Get all visible reviews for a salon
List<Review> findBySalon_IdAndIsVisibleTrue(String salonId);

// Get reviews sorted by date (newest first)
List<Review> findBySalon_IdAndIsVisibleTrueOrderByReviewDateDesc(String salonId);

// Get user's reviews
List<Review> findByUser_Id(String userId);
```

#### Filtered Queries
```java
// Reviews by specific rating
List<Review> findBySalon_IdAndRatingAndIsVisibleTrue(String salonId, Integer rating);

// Only verified reviews
List<Review> findBySalon_IdAndIsVerifiedTrueAndIsVisibleTrue(String salonId);

// Sort by helpful votes
List<Review> findBySalon_IdAndIsVisibleTrueOrderByHelpfulCountDesc(String salonId);
```

#### Existence Checks
```java
// Prevent duplicate reviews
Boolean existsBySalon_IdAndUser_Id(String salonId, String userId);

// Check appointment already reviewed
Boolean existsByAppointment_Id(String appointmentId);
```

#### Single Result
```java
// Find user's review for a salon
Optional<Review> findBySalon_IdAndUser_Id(String salonId, String userId);
```

#### Count Queries
```java
// Total reviews for salon
Long countBySalon_IdAndIsVisibleTrue(String salonId);

// Count by rating (for distribution)
Long countBySalon_IdAndRatingAndIsVisibleTrue(String salonId, Integer rating);
```

#### Aggregation Queries
```java
// Calculate average rating
Optional<RatingStatisticsDTO> calculateAverageRating(String salonId);

// Get full rating distribution (1-5 star breakdown)
Optional<RatingStatisticsDTO> getRatingDistribution(String salonId);
```

---

## 📦 Data Transfer Objects (DTOs)

### 1. CreateReviewDTO
**Purpose:** Create new review (POST endpoint)

```java
@Data
@Builder
public class CreateReviewDTO {
    @NotBlank String salonId;           // Required
    String userId;                       // Optional (guest if null)
    @NotBlank @Size(min=2, max=100) String reviewerName;
    @NotBlank @Email String reviewerEmail;
    @NotNull @Min(1) @Max(5) Integer rating;
    @NotBlank @Size(min=10, max=500) String comment;
    String appointmentId;                // Optional (verified review)
}
```

### 2. UpdateReviewDTO
**Purpose:** Update existing review (PUT endpoint)

```java
@Data
public class UpdateReviewDTO {
    @NotNull @Min(1) @Max(5) Integer rating;
    @NotNull @Size(min=10, max=500) String comment;
}
```

**Restrictions:**
- Only rating and comment can be updated
- Cannot change reviewer info or salon
- Only allowed within 24 hours of creation

### 3. ReviewResponseDTO
**Purpose:** Return review data (GET endpoint)

```java
@Data
@Builder
public class ReviewResponseDTO {
    String id;
    String salonId;
    String salonName;          // Populated from Salon
    String userId;
    String reviewerName;
    Integer rating;
    String comment;
    LocalDateTime reviewDate;
    LocalDateTime lastModified;
    Boolean isVerified;
    Integer helpfulCount;
    Boolean isGuestReview;
    Boolean isEditable;
    String ownerResponse;
    LocalDateTime ownerResponseDate;
    String userAvatar;         // Can be populated from User
}
```

**Hides:** `reportCount`, `moderatorNotes` (internal fields)

### 4. RatingStatisticsDTO
**Purpose:** Aggregated rating data (GET statistics endpoint)

```java
@Data
@Builder
public class RatingStatisticsDTO {
    String salonId;
    Double averageRating;      // e.g., 4.8
    Long totalReviews;         // e.g., 124
    
    // Distribution
    Long fiveStars;
    Long fourStars;
    Long threeStars;
    Long twoStars;
    Long oneStar;
    
    // Calculated percentages
    Double getFiveStarPercentage();
    Double getFourStarPercentage();
    // ... etc
}
```

---

## 📊 Sample Data Structure

### MongoDB Document Example:

```json
{
  "_id": "review123",
  "salon": {
    "$ref": "salons",
    "$id": "salon456"
  },
  "user": {
    "$ref": "users",
    "$id": "user789"
  },
  "reviewerName": "Sarah Johnson",
  "reviewerEmail": "sarah@example.com",
  "rating": 5,
  "comment": "Amazing service! The staff was very professional and friendly. Highly recommend for haircuts.",
  "reviewDate": "2025-10-08T10:30:00",
  "lastModified": null,
  "isVerified": true,
  "appointment": {
    "$ref": "appointments",
    "$id": "appt101"
  },
  "isVisible": true,
  "helpfulCount": 12,
  "reportCount": 0,
  "moderatorNotes": null,
  "ownerResponse": "Thank you for your kind words! We're glad you enjoyed your visit.",
  "ownerResponseDate": "2025-10-09T14:20:00"
}
```

---

## 🎯 Business Rules

### Review Submission Rules:

1. **One Review Per User Per Salon**
   - Enforced by unique compound index: `salon + user`
   - Guest reviews (no user) are allowed multiple times
   - Check with: `existsBySalon_IdAndUser_Id()`

2. **Rating Validation**
   - Must be integer between 1 and 5
   - Required field (cannot be null)

3. **Comment Quality**
   - Minimum 10 characters (ensure meaningful feedback)
   - Maximum 500 characters (keep reviews concise)
   - Required field

4. **Reviewer Information**
   - **Registered Users:** Auto-populate from User profile
   - **Guest Reviews:** Require name and email input
   - Email validation enforced

5. **Review Editing**
   - Allowed within 24 hours of creation
   - Only rating and comment can be changed
   - `lastModified` timestamp updated automatically

### Verification Rules:

1. **Verified Reviews**
   - Must be linked to a completed appointment
   - Set `isVerified = true`
   - Display special badge in UI

2. **Appointment Link**
   - One review per appointment
   - Check with: `existsByAppointment_Id()`

### Moderation Rules:

1. **Visibility Control**
   - `isVisible = true`: Publicly visible (default)
   - `isVisible = false`: Hidden by moderator
   - Queries filter by `isVisible` automatically

2. **Spam Detection**
   - `reportCount` tracks user reports
   - Auto-hide if `reportCount >= 5` (configurable)
   - Use: `findHighlyReportedReviews(threshold)`

3. **Helpful Voting**
   - Users can mark reviews as helpful
   - Increment with `incrementHelpfulCount()`
   - Sort popular reviews: `OrderByHelpfulCountDesc`

---

## 🚀 API Endpoint Design

### Recommended REST Endpoints:

```
POST   /api/reviews                    - Create new review
GET    /api/reviews/{id}               - Get single review
PUT    /api/reviews/{id}               - Update review (within 24h)
DELETE /api/reviews/{id}               - Delete review (soft delete)

GET    /api/reviews/salon/{salonId}    - Get all reviews for salon
GET    /api/reviews/salon/{salonId}/statistics  - Get rating stats
GET    /api/reviews/user/{userId}      - Get user's reviews

POST   /api/reviews/{id}/helpful       - Mark review as helpful
POST   /api/reviews/{id}/report        - Report review as spam

GET    /api/reviews/salon/{salonId}/verified  - Get verified reviews only
GET    /api/reviews/salon/{salonId}/rating/{rating}  - Filter by rating

// Admin endpoints
GET    /api/admin/reviews/reported     - Get highly reported reviews
PUT    /api/admin/reviews/{id}/hide    - Hide/unhide review
PUT    /api/admin/reviews/{id}/notes   - Add moderator notes
```

---

## 📈 Performance Optimizations

### 1. Indexed Queries
```java
// ✅ Uses salon index
findBySalon_IdAndIsVisibleTrue(salonId)

// ✅ Uses compound index (salon + reviewDate)
findBySalon_IdAndIsVisibleTrueOrderByReviewDateDesc(salonId)

// ✅ Uses user index
findByUser_Id(userId)
```

### 2. Cached Ratings in Salon
```java
// Instead of expensive aggregation every time:
@Aggregation calculateAverageRating(salonId)  // Slow

// Use cached values for fast reads:
Salon salon = salonRepository.findById(id);
Double rating = salon.getAverageRating();     // Fast!
Long total = salon.getTotalReviews();
```

**Update Strategy:**
- Update cache asynchronously when review is added/modified
- Use service layer method: `updateSalonRatingCache(salonId)`

### 3. Projection Queries
```java
// Only fetch rating field (not full documents)
@Query(value = "...", fields = "{ 'rating': 1 }")
List<Review> findRatingsBySalonId(String salonId);
```

---

## 🧪 Sample Data Seeder

### Example Code for Testing:

```java
@Component
public class ReviewDataSeeder {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private SalonRepository salonRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public void seedReviews() {
        // Clear existing reviews
        reviewRepository.deleteAll();
        
        // Get sample salon and users
        Salon salon = salonRepository.findAll().get(0);
        User user1 = userRepository.findAll().get(0);
        User user2 = userRepository.findAll().get(1);
        
        // Create 5-star review
        Review review1 = Review.builder()
            .salon(salon)
            .user(user1)
            .reviewerName(user1.getUsername())
            .reviewerEmail(user1.getEmail())
            .rating(5)
            .comment("Excellent service! The staff was incredibly professional and friendly. My haircut turned out exactly as I wanted. The salon was clean and modern. Highly recommend!")
            .reviewDate(LocalDateTime.now().minusDays(5))
            .isVerified(true)
            .isVisible(true)
            .helpfulCount(15)
            .build();
        
        // Create 4-star review
        Review review2 = Review.builder()
            .salon(salon)
            .user(user2)
            .reviewerName(user2.getUsername())
            .reviewerEmail(user2.getEmail())
            .rating(4)
            .comment("Great experience overall. The stylist was skilled and listened to what I wanted. Only minor issue was the wait time, but the quality of service made up for it.")
            .reviewDate(LocalDateTime.now().minusDays(3))
            .isVerified(true)
            .isVisible(true)
            .helpfulCount(8)
            .build();
        
        // Create guest review (no user)
        Review review3 = Review.builder()
            .salon(salon)
            .user(null)  // Guest review
            .reviewerName("John Smith")
            .reviewerEmail("john.smith@example.com")
            .rating(5)
            .comment("First time visiting this salon and I'm impressed! The atmosphere is relaxing and the staff are welcoming. Will definitely return.")
            .reviewDate(LocalDateTime.now().minusDays(1))
            .isVerified(false)
            .isVisible(true)
            .helpfulCount(3)
            .build();
        
        // Create 3-star review
        Review review4 = Review.builder()
            .salon(salon)
            .user(null)
            .reviewerName("Emily Davis")
            .reviewerEmail("emily.d@example.com")
            .rating(3)
            .comment("Service was okay. The haircut was decent but not exactly what I asked for. Pricing was fair but I expected better attention to detail.")
            .reviewDate(LocalDateTime.now().minusHours(12))
            .isVerified(false)
            .isVisible(true)
            .helpfulCount(2)
            .build();
        
        // Save all reviews
        reviewRepository.saveAll(Arrays.asList(
            review1, review2, review3, review4
        ));
        
        System.out.println("✅ Seeded " + reviewRepository.count() + " reviews");
    }
}
```

---

## ✅ Implementation Checklist

### Database Layer:
- [x] Enhanced Review entity with all fields
- [x] Added validation annotations
- [x] Defined compound indexes
- [x] Added business logic methods
- [x] Updated Salon model with rating cache

### Repository Layer:
- [x] Basic CRUD queries
- [x] Filtered queries (by rating, verified, etc.)
- [x] Existence checks (duplicate prevention)
- [x] Count queries
- [x] Aggregation queries (statistics)
- [x] Custom MongoDB queries

### DTO Layer:
- [x] CreateReviewDTO (POST)
- [x] UpdateReviewDTO (PUT)
- [x] ReviewResponseDTO (GET)
- [x] RatingStatisticsDTO (Statistics)

### Documentation:
- [x] Complete field descriptions
- [x] Index explanations
- [x] Business rules documentation
- [x] API endpoint design
- [x] Sample data examples
- [x] Performance optimization notes

### Next Steps (Implementation):
- [ ] Create ReviewService
- [ ] Create ReviewController
- [ ] Add validation logic
- [ ] Implement rating cache update
- [ ] Add security (user ownership checks)
- [ ] Create integration tests
- [ ] Add sample data seeder
- [ ] Create admin moderation endpoints

---

## 📚 Additional Resources

### Related Files:
- `models/Review.java` - Main entity
- `repositories/ReviewRepository.java` - Database queries
- `dto/CreateReviewDTO.java` - Create review request
- `dto/UpdateReviewDTO.java` - Update review request
- `dto/ReviewResponseDTO.java` - Review response
- `dto/RatingStatisticsDTO.java` - Statistics response
- `models/Salon.java` - Enhanced with rating cache

### Database Collections:
- `reviews` - Review documents
- `salons` - Salon documents (with cached ratings)
- `users` - User documents (reviewer info)
- `appointments` - Appointment documents (for verification)

---

**Version:** 2.0  
**Last Updated:** October 10, 2025  
**Author:** Salon Booking System Team
