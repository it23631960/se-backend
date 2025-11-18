package com.example.salon_booking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.salon_booking.dto.CreateReviewDTO;
import com.example.salon_booking.dto.RatingStatisticsDTO;
import com.example.salon_booking.dto.ReviewResponseDTO;
import com.example.salon_booking.dto.UpdateReviewDTO;
import com.example.salon_booking.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Review operations
 * 
 * Provides endpoints for:
 * - Getting reviews for a salon (with pagination and sorting)
 * - Getting rating statistics
 * - Creating new reviews
 * - Updating existing reviews
 * - Deleting reviews
 * - Checking for existing reviews
 * - Marking reviews as helpful
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"})
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    
    private final ReviewService reviewService;
    
    /**
     * Get all reviews for a specific salon
     * 
     * @param salonId Salon identifier
     * @param sort Sort option: recent, highest, lowest (default: recent)
     * @param page Page number (0-indexed, default: 0)
     * @param size Number of items per page (default: 10)
     * @return Paginated list of reviews
     */
    @GetMapping("/salon/{salonId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getSalonReviews(
            @PathVariable String salonId,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching reviews for salon: {}, sort: {}, page: {}, size: {}", salonId, sort, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponseDTO> reviews = reviewService.getSalonReviews(salonId, sort, pageable);
        
        log.info("Found {} reviews for salon: {}", reviews.getTotalElements(), salonId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Get rating statistics for a specific salon
     * 
     * @param salonId Salon identifier
     * @return Rating statistics including average, total, and distribution
     */
    @GetMapping("/salon/{salonId}/summary")
    public ResponseEntity<RatingStatisticsDTO> getRatingSummary(
            @PathVariable String salonId
    ) {
        log.info("Fetching rating summary for salon: {}", salonId);
        
        RatingStatisticsDTO summary = reviewService.getRatingSummary(salonId);
        
        log.info("Rating summary for salon {}: avg={}, total={}", 
                salonId, summary.getAverageRating(), summary.getTotalReviews());
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Create a new review
     * 
     * @param request Review data
     * @return Created review with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(
            @Valid @RequestBody CreateReviewDTO request
    ) {
        log.info("Creating review for salon: {} by user: {}", 
                request.getSalonId(), request.getReviewerName());
        
        ReviewResponseDTO review = reviewService.createReview(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Review submitted successfully");
        response.put("review", review);
        
        log.info("Review created successfully with ID: {}", review.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get a specific review by ID
     * 
     * @param reviewId Review identifier
     * @return Review details
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReview(
            @PathVariable String reviewId
    ) {
        log.info("Fetching review with ID: {}", reviewId);
        
        ReviewResponseDTO review = reviewService.getReviewById(reviewId);
        
        return ResponseEntity.ok(review);
    }
    
    /**
     * Update an existing review
     * 
     * @param reviewId Review identifier
     * @param request Updated review data
     * @return Updated review
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<Map<String, Object>> updateReview(
            @PathVariable String reviewId,
            @Valid @RequestBody UpdateReviewDTO request
    ) {
        log.info("Updating review with ID: {}", reviewId);
        
        ReviewResponseDTO review = reviewService.updateReview(reviewId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Review updated successfully");
        response.put("review", review);
        
        log.info("Review updated successfully: {}", reviewId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete a review
     * 
     * @param reviewId Review identifier
     * @return Success message
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, String>> deleteReview(
            @PathVariable String reviewId
    ) {
        log.info("Deleting review with ID: {}", reviewId);
        
        reviewService.deleteReview(reviewId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Review deleted successfully");
        
        log.info("Review deleted successfully: {}", reviewId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all reviews by a specific user
     * 
     * @param userId User identifier
     * @return List of user's reviews
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getUserReviews(
            @PathVariable String userId
    ) {
        log.info("Fetching reviews for user: {}", userId);
        
        List<ReviewResponseDTO> reviews = reviewService.getUserReviews(userId);
        
        log.info("Found {} reviews for user: {}", reviews.size(), userId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Check if a user has already reviewed a salon
     * 
     * @param salonId Salon identifier
     * @param userId User identifier (optional)
     * @return Map with hasReviewed flag and review details if exists
     */
    @GetMapping("/check-existing")
    public ResponseEntity<Map<String, Object>> checkExistingReview(
            @RequestParam String salonId,
            @RequestParam(required = false) String userId
    ) {
        log.info("Checking existing review for salon: {}, user: {}", salonId, userId);
        
        Map<String, Object> response = new HashMap<>();
        
        if (userId != null) {
            Boolean hasReviewed = reviewService.hasUserReviewedSalon(salonId, userId);
            response.put("hasReviewed", hasReviewed);
            
            if (hasReviewed) {
                ReviewResponseDTO review = reviewService.getUserReviewForSalon(salonId, userId);
                response.put("reviewId", review.getId());
                response.put("review", review);
            }
        } else {
            response.put("hasReviewed", false);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark a review as helpful
     * 
     * @param reviewId Review identifier
     * @return Updated helpful count
     */
    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<Map<String, Integer>> markReviewHelpful(
            @PathVariable String reviewId
    ) {
        log.info("Marking review as helpful: {}", reviewId);
        
        Integer helpfulCount = reviewService.incrementHelpfulCount(reviewId);
        
        Map<String, Integer> response = new HashMap<>();
        response.put("helpfulCount", helpfulCount);
        
        log.info("Review {} helpful count updated to: {}", reviewId, helpfulCount);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Report a review (for moderation)
     * 
     * @param reviewId Review identifier
     * @return Success message
     */
    @PostMapping("/{reviewId}/report")
    public ResponseEntity<Map<String, String>> reportReview(
            @PathVariable String reviewId
    ) {
        log.info("Reporting review: {}", reviewId);
        
        reviewService.incrementReportCount(reviewId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Review reported successfully. Our team will review it.");
        
        log.info("Review {} has been reported", reviewId);
        return ResponseEntity.ok(response);
    }
}
