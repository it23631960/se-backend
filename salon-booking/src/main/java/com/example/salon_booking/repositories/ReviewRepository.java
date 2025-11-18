package com.example.salon_booking.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.salon_booking.dto.RatingStatisticsDTO;
import com.example.salon_booking.models.Review;

/**
 * Repository interface for Review entity.
 * Provides database operations for review management including
 * CRUD operations, custom queries, and aggregation pipelines.
 * 
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Find reviews by salon, user, rating</li>
 *   <li>Sorting by date, rating, helpful count</li>
 *   <li>Duplicate prevention checks</li>
 *   <li>Statistics and aggregation queries</li>
 *   <li>Moderation support (visible/hidden reviews)</li>
 * </ul>
 * 
 * @author Salon Booking System
 * @version 2.0
 * @since 2025-10-10
 */
@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    
    // ==================== BASIC QUERIES ====================
    
    /**
     * Find all visible reviews for a specific salon.
     * Only returns reviews where isVisible = true.
     * Uses nested property syntax for @DBRef relationships.
     * 
     * @param salonId Salon ID
     * @return List of visible reviews
     */
    List<Review> findBySalon_IdAndIsVisibleTrue(String salonId);
    
    /**
     * Find all visible reviews for a specific salon with pagination and sorting.
     * Only returns reviews where isVisible = true.
     * 
     * @param salonId Salon ID
     * @param pageable Pagination and sorting information
     * @return Page of visible reviews
     */
    Page<Review> findBySalon_IdAndIsVisibleTrue(String salonId, Pageable pageable);
    
    /**
     * Find all reviews for a salon sorted by date (newest first).
     * Only returns visible reviews.
     * 
     * @param salonId Salon ID
     * @return List of reviews sorted by reviewDate descending
     */
    List<Review> findBySalon_IdAndIsVisibleTrueOrderByReviewDateDesc(String salonId);
    
    /**
     * Find all reviews by a specific user.
     * Returns both visible and hidden reviews for the user's own view.
     * 
     * @param userId User ID
     * @return List of user's reviews
     */
    List<Review> findByUser_Id(String userId);
    
    /**
     * Find all reviews by a specific user sorted by date (newest first).
     * 
     * @param userId User ID
     * @return List of user's reviews sorted by date
     */
    List<Review> findByUser_IdOrderByReviewDateDesc(String userId);
    
    // ==================== FILTERED QUERIES ====================
    
    /**
     * Find reviews by salon and specific rating.
     * Useful for filtering by star rating (e.g., "Show only 5-star reviews").
     * 
     * @param salonId Salon ID
     * @param rating Star rating (1-5)
     * @return List of reviews with specified rating
     */
    List<Review> findBySalon_IdAndRatingAndIsVisibleTrue(String salonId, Integer rating);
    
    /**
     * Find only verified reviews for a salon.
     * Returns reviews linked to actual appointments.
     * 
     * @param salonId Salon ID
     * @return List of verified reviews
     */
    List<Review> findBySalon_IdAndIsVerifiedTrueAndIsVisibleTrue(String salonId);
    
    /**
     * Find reviews sorted by helpful count (most helpful first).
     * Useful for displaying top-rated reviews.
     * 
     * @param salonId Salon ID
     * @return List of reviews sorted by helpfulness
     */
    List<Review> findBySalon_IdAndIsVisibleTrueOrderByHelpfulCountDesc(String salonId);
    
    /**
     * Find reviews with high report count (potential spam).
     * Used by moderation system to identify problematic reviews.
     * 
     * @param threshold Minimum report count
     * @return List of highly reported reviews
     */
    @Query("{ 'reportCount': { $gte: ?0 }, 'isVisible': true }")
    List<Review> findHighlyReportedReviews(Integer threshold);
    
    // ==================== EXISTENCE CHECKS ====================
    
    /**
     * Check if a user has already reviewed a specific salon.
     * Prevents duplicate reviews (one review per user per salon).
     * 
     * @param salonId Salon ID
     * @param userId User ID
     * @return true if review exists, false otherwise
     */
    Boolean existsBySalon_IdAndUser_Id(String salonId, String userId);
    
    /**
     * Check if a review exists for a specific appointment.
     * Prevents multiple reviews for the same appointment.
     * 
     * @param appointmentId Appointment ID
     * @return true if review exists, false otherwise
     */
    Boolean existsByAppointment_Id(String appointmentId);
    
    // ==================== SINGLE RESULT QUERIES ====================
    
    /**
     * Find a user's review for a specific salon.
     * Returns the review if it exists.
     * 
     * @param salonId Salon ID
     * @param userId User ID
     * @return Optional containing the review if found
     */
    Optional<Review> findBySalon_IdAndUser_Id(String salonId, String userId);
    
    /**
     * Find review by appointment ID.
     * 
     * @param appointmentId Appointment ID
     * @return Optional containing the review if found
     */
    Optional<Review> findByAppointment_Id(String appointmentId);
    
    // ==================== COUNT QUERIES ====================
    
    /**
     * Count total number of visible reviews for a salon.
     * 
     * @param salonId Salon ID
     * @return Number of reviews
     */
    Long countBySalon_IdAndIsVisibleTrue(String salonId);
    
    /**
     * Count reviews by rating for a salon.
     * Used to build rating distribution statistics.
     * 
     * @param salonId Salon ID
     * @param rating Star rating (1-5)
     * @return Number of reviews with specified rating
     */
    Long countBySalon_IdAndRatingAndIsVisibleTrue(String salonId, Integer rating);
    
    /**
     * Count verified reviews for a salon.
     * 
     * @param salonId Salon ID
     * @return Number of verified reviews
     */
    Long countBySalon_IdAndIsVerifiedTrueAndIsVisibleTrue(String salonId);
    
    /**
     * Count total reviews by a user.
     * 
     * @param userId User ID
     * @return Number of reviews written by user
     */
    Long countByUser_Id(String userId);
    
    // ==================== AGGREGATION QUERIES ====================
    
    /**
     * Calculate average rating for a salon using MongoDB aggregation.
     * Returns average rating and total count in a single query.
     * 
     * <p>Pipeline:</p>
     * <ol>
     *   <li>Match reviews for the salon where isVisible = true</li>
     *   <li>Group by salon and calculate average rating and count</li>
     * </ol>
     * 
     * @param salonId Salon ID
     * @return RatingStatisticsDTO with averageRating and totalReviews
     */
    @Aggregation(pipeline = {
        "{ '$match': { 'salon.$id': ?0, 'isVisible': true } }",
        "{ '$group': { " +
        "    '_id': null, " +
        "    'averageRating': { '$avg': '$rating' }, " +
        "    'totalReviews': { '$sum': 1 } " +
        "} }",
        "{ '$project': { " +
        "    '_id': 0, " +
        "    'averageRating': 1, " +
        "    'totalReviews': 1 " +
        "} }"
    })
    Optional<RatingStatisticsDTO> calculateAverageRating(String salonId);
    
    /**
     * Get complete rating distribution for a salon.
     * Returns count of reviews for each star rating (1-5).
     * 
     * <p>Pipeline:</p>
     * <ol>
     *   <li>Match reviews for the salon where isVisible = true</li>
     *   <li>Group by rating and count</li>
     *   <li>Project into distribution format</li>
     * </ol>
     * 
     * @param salonId Salon ID
     * @return RatingStatisticsDTO with full distribution
     */
    @Aggregation(pipeline = {
        "{ '$match': { 'salon.$id': ?0, 'isVisible': true } }",
        "{ '$facet': {" +
        "    'stats': [" +
        "        { '$group': { " +
        "            '_id': null, " +
        "            'averageRating': { '$avg': '$rating' }, " +
        "            'totalReviews': { '$sum': 1 } " +
        "        } }" +
        "    ]," +
        "    'distribution': [" +
        "        { '$group': { '_id': '$rating', 'count': { '$sum': 1 } } }," +
        "        { '$sort': { '_id': -1 } }" +
        "    ]" +
        "} }",
        "{ '$project': {" +
        "    'averageRating': { '$arrayElemAt': ['$stats.averageRating', 0] }," +
        "    'totalReviews': { '$arrayElemAt': ['$stats.totalReviews', 0] }," +
        "    'fiveStars': { " +
        "        '$sum': { " +
        "            '$cond': [{ '$eq': [{ '$arrayElemAt': ['$distribution._id', 0] }, 5] }, " +
        "                     { '$arrayElemAt': ['$distribution.count', 0] }, 0] " +
        "        } " +
        "    }," +
        "    'fourStars': { " +
        "        '$sum': { " +
        "            '$map': { " +
        "                'input': '$distribution', " +
        "                'as': 'item', " +
        "                'in': { '$cond': [{ '$eq': ['$$item._id', 4] }, '$$item.count', 0] } " +
        "            } " +
        "        } " +
        "    }," +
        "    'threeStars': { " +
        "        '$sum': { " +
        "            '$map': { " +
        "                'input': '$distribution', " +
        "                'as': 'item', " +
        "                'in': { '$cond': [{ '$eq': ['$$item._id', 3] }, '$$item.count', 0] } " +
        "            } " +
        "        } " +
        "    }," +
        "    'twoStars': { " +
        "        '$sum': { " +
        "            '$map': { " +
        "                'input': '$distribution', " +
        "                'as': 'item', " +
        "                'in': { '$cond': [{ '$eq': ['$$item._id', 2] }, '$$item.count', 0] } " +
        "            } " +
        "        } " +
        "    }," +
        "    'oneStar': { " +
        "        '$sum': { " +
        "            '$map': { " +
        "                'input': '$distribution', " +
        "                'as': 'item', " +
        "                'in': { '$cond': [{ '$eq': ['$$item._id', 1] }, '$$item.count', 0] } " +
        "            } " +
        "        } " +
        "    }" +
        "} }"
    })
    Optional<RatingStatisticsDTO> getRatingDistribution(String salonId);
    
    // ==================== CUSTOM QUERIES ====================
    
    /**
     * Find reviews using custom MongoDB query.
     * Alternative to method naming convention.
     * 
     * @param salonId Salon ID
     * @return List of reviews
     */
    @Query("{ 'salon.$id': ?0, 'isVisible': true }")
    List<Review> findBySalonIdQuery(String salonId);
    
    /**
     * Find ratings only for a salon (optimized query).
     * Only returns the rating field to calculate statistics efficiently.
     * 
     * @param salonId Salon ID
     * @return List of reviews with only rating field populated
     */
    @Query(value = "{ 'salon.$id': ?0, 'isVisible': true }", fields = "{ 'rating': 1 }")
    List<Review> findRatingsBySalonId(String salonId);
    
    /**
     * Find recent reviews across all salons (for admin dashboard).
     * Returns the most recent reviews regardless of salon.
     * 
     * @return List of recent reviews
     */
    @Query(value = "{ 'isVisible': true }", sort = "{ 'reviewDate': -1 }")
    List<Review> findRecentReviews();
    
    /**
     * Delete all reviews for a specific salon.
     * Used when a salon is deleted from the system.
     * 
     * @param salonId Salon ID
     */
    void deleteBySalon_Id(String salonId);
    
    /**
     * Delete all reviews by a specific user.
     * Used when a user account is deleted.
     * 
     * @param userId User ID
     */
    void deleteByUser_Id(String userId);
}
