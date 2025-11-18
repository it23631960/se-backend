package com.example.salon_booking.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.salon_booking.dto.CreateReviewDTO;
import com.example.salon_booking.dto.RatingStatisticsDTO;
import com.example.salon_booking.dto.ReviewResponseDTO;
import com.example.salon_booking.dto.UpdateReviewDTO;
import com.example.salon_booking.exception.DuplicateReviewException;
import com.example.salon_booking.exception.ResourceNotFoundException;
import com.example.salon_booking.exception.ReviewEditTimeExpiredException;
import com.example.salon_booking.models.Review;
import com.example.salon_booking.models.Salon;
import com.example.salon_booking.repositories.ReviewRepository;
import com.example.salon_booking.repositories.SalonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service layer for Review business logic
 * 
 * Handles:
 * - CRUD operations for reviews
 * - Duplicate prevention
 * - Rating calculations and caching
 * - Time-based edit restrictions
 * - Helpful voting and reporting
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final SalonRepository salonRepository;
    
    private static final int EDIT_TIME_LIMIT_HOURS = 24;
    
    /**
     * Get paginated reviews for a salon with sorting
     */
    public Page<ReviewResponseDTO> getSalonReviews(String salonId, String sortBy, Pageable pageable) {
        log.debug("Getting reviews for salon: {} with sort: {}", salonId, sortBy);
        
        // Validate salon exists
        if (!salonRepository.existsById(salonId)) {
            throw new ResourceNotFoundException("Salon not found with id: " + salonId);
        }
        
        // Create sort based on sortBy parameter
        Sort sort = createSort(sortBy);
        Pageable sortedPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            sort
        );
        
        // Get reviews
        Page<Review> reviews = reviewRepository.findBySalon_IdAndIsVisibleTrue(salonId, sortedPageable);
        
        // Convert to DTOs
        return reviews.map(this::convertToDTO);
    }
    
    /**
     * Get rating statistics for a salon
     */
    public RatingStatisticsDTO getRatingSummary(String salonId) {
        log.debug("Getting rating summary for salon: {}", salonId);
        
        // Validate salon exists
        if (!salonRepository.existsById(salonId)) {
            throw new ResourceNotFoundException("Salon not found with id: " + salonId);
        }
        
        // Get all visible reviews for the salon
        List<Review> reviews = reviewRepository.findBySalon_IdAndIsVisibleTrue(salonId);
        
        if (reviews.isEmpty()) {
            return RatingStatisticsDTO.builder()
                    .salonId(salonId)
                    .averageRating(0.0)
                    .totalReviews(0L)
                    .fiveStars(0L)
                    .fourStars(0L)
                    .threeStars(0L)
                    .twoStars(0L)
                    .oneStar(0L)
                    .build();
        }
        
        // Calculate statistics
        long totalReviews = reviews.size();
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        
        // Count reviews by rating
        Map<Integer, Long> distribution = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
        
        return RatingStatisticsDTO.builder()
                .salonId(salonId)
                .averageRating(Math.round(averageRating * 10.0) / 10.0) // Round to 1 decimal
                .totalReviews(totalReviews)
                .fiveStars(distribution.getOrDefault(5, 0L))
                .fourStars(distribution.getOrDefault(4, 0L))
                .threeStars(distribution.getOrDefault(3, 0L))
                .twoStars(distribution.getOrDefault(2, 0L))
                .oneStar(distribution.getOrDefault(1, 0L))
                .build();
    }
    
    /**
     * Create a new review
     */
    @Transactional
    public ReviewResponseDTO createReview(CreateReviewDTO dto) {
        log.info("Creating review for salon: {} by {}", dto.getSalonId(), dto.getReviewerName());
        
        // 1. Validate salon exists
        Salon salon = salonRepository.findById(dto.getSalonId())
                .orElseThrow(() -> new ResourceNotFoundException("Salon not found with id: " + dto.getSalonId()));
        
        // 2. Check for duplicate review (if user ID provided)
        if (dto.getUserId() != null) {
            Boolean exists = reviewRepository.existsBySalon_IdAndUser_Id(dto.getSalonId(), dto.getUserId());
            if (exists) {
                throw new DuplicateReviewException("You have already reviewed this salon");
            }
        }
        
        // 3. Create review entity
        Review.ReviewBuilder reviewBuilder = Review.builder()
                .salon(salon)
                .reviewerName(dto.getReviewerName())
                .reviewerEmail(dto.getReviewerEmail())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .reviewDate(LocalDateTime.now())
                .lastModified(LocalDateTime.now())
                .isVerified(dto.getAppointmentId() != null) // Verified if linked to appointment
                .isVisible(true)
                .helpfulCount(0)
                .reportCount(0);
        
        // Set user reference if userId provided (optional for guest reviews)
        // Note: In production, you should fetch the User object from UserRepository
        // For now, we'll just use the user field as null for guest reviews
        
        Review review = reviewBuilder.build();
        
        // 4. Save review
        review = reviewRepository.save(review);
        log.info("Review created with ID: {}", review.getId());
        
        // 5. Update salon's cached rating asynchronously
        updateSalonRatingAsync(dto.getSalonId());
        
        // 6. Return DTO
        return convertToDTO(review);
    }
    
    /**
     * Get a review by ID
     */
    public ReviewResponseDTO getReviewById(String reviewId) {
        log.debug("Getting review by ID: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        return convertToDTO(review);
    }
    
    /**
     * Update an existing review
     */
    @Transactional
    public ReviewResponseDTO updateReview(String reviewId, UpdateReviewDTO dto) {
        log.info("Updating review: {}", reviewId);
        
        // 1. Find review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        // 2. Check if review is editable (within 24 hours)
        if (!review.isEditable()) {
            long hoursSinceCreation = ChronoUnit.HOURS.between(review.getReviewDate(), LocalDateTime.now());
            throw new ReviewEditTimeExpiredException(
                    String.format("Reviews can only be edited within %d hours of submission. " +
                            "This review was created %d hours ago.", EDIT_TIME_LIMIT_HOURS, hoursSinceCreation)
            );
        }
        
        // 3. Update fields
        if (dto.getRating() != null) {
            review.setRating(dto.getRating());
        }
        if (dto.getComment() != null && !dto.getComment().isBlank()) {
            review.setComment(dto.getComment());
        }
        review.setLastModified(LocalDateTime.now());
        
        // 4. Save
        review = reviewRepository.save(review);
        log.info("Review updated: {}", reviewId);
        
        // 5. Update salon rating cache
        updateSalonRatingAsync(review.getSalon().getId());
        
        return convertToDTO(review);
    }
    
    /**
     * Delete a review (soft delete - mark as invisible)
     */
    @Transactional
    public void deleteReview(String reviewId) {
        log.info("Deleting review: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        String salonId = review.getSalon().getId();
        
        // Soft delete - mark as invisible
        review.setIsVisible(false);
        reviewRepository.save(review);
        
        log.info("Review marked as invisible: {}", reviewId);
        
        // Update salon rating cache
        updateSalonRatingAsync(salonId);
    }
    
    /**
     * Get all reviews by a user
     */
    public List<ReviewResponseDTO> getUserReviews(String userId) {
        log.debug("Getting reviews for user: {}", userId);
        
        List<Review> reviews = reviewRepository.findByUser_Id(userId);
        
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if user has reviewed a salon
     */
    public Boolean hasUserReviewedSalon(String salonId, String userId) {
        return reviewRepository.existsBySalon_IdAndUser_Id(salonId, userId);
    }
    
    /**
     * Get user's review for a specific salon
     */
    public ReviewResponseDTO getUserReviewForSalon(String salonId, String userId) {
        Review review = reviewRepository.findBySalon_IdAndUser_Id(salonId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found for this user and salon"));
        
        return convertToDTO(review);
    }
    
    /**
     * Increment helpful count for a review
     */
    @Transactional
    public Integer incrementHelpfulCount(String reviewId) {
        log.debug("Incrementing helpful count for review: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        review.incrementHelpfulCount();
        review = reviewRepository.save(review);
        
        return review.getHelpfulCount();
    }
    
    /**
     * Increment report count for a review
     */
    @Transactional
    public Integer incrementReportCount(String reviewId) {
        log.debug("Incrementing report count for review: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        review.incrementReportCount();
        review = reviewRepository.save(review);
        
        // Auto-hide review if reported too many times (e.g., 5 times)
        if (review.getReportCount() >= 5 && review.getIsVisible()) {
            log.warn("Review {} has been reported {} times, marking as invisible", 
                    reviewId, review.getReportCount());
            review.setIsVisible(false);
            review.setModeratorNotes("Auto-hidden due to multiple reports");
            reviewRepository.save(review);
        }
        
        return review.getReportCount();
    }
    
    /**
     * Update salon's cached rating (async)
     */
    @Async
    protected void updateSalonRatingAsync(String salonId) {
        try {
            log.debug("Updating cached rating for salon: {}", salonId);
            
            RatingStatisticsDTO stats = getRatingSummary(salonId);
            
            Salon salon = salonRepository.findById(salonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Salon not found"));
            
            salon.setAverageRating(stats.getAverageRating());
            salon.setTotalReviews(stats.getTotalReviews());
            
            salonRepository.save(salon);
            
            log.info("Updated cached rating for salon {}: avg={}, total={}", 
                    salonId, stats.getAverageRating(), stats.getTotalReviews());
        } catch (Exception e) {
            log.error("Failed to update salon rating cache: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Create Sort object based on sort option
     */
    private Sort createSort(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "highest" -> Sort.by("rating").descending().and(Sort.by("reviewDate").descending());
            case "lowest" -> Sort.by("rating").ascending().and(Sort.by("reviewDate").descending());
            default -> Sort.by("reviewDate").descending(); // "recent"
        };
    }
    
    /**
     * Convert Review entity to DTO
     */
    private ReviewResponseDTO convertToDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .salonId(review.getSalon().getId())
                .salonName(review.getSalon().getName())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .reviewerName(review.getReviewerName())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .lastModified(review.getLastModified())
                .isVerified(review.getIsVerified())
                .helpfulCount(review.getHelpfulCount())
                .isGuestReview(review.isGuestReview())
                .isEditable(review.isEditable())
                .ownerResponse(review.getOwnerResponse())
                .ownerResponseDate(review.getOwnerResponseDate())
                .userAvatar(review.getUser() != null ? null : null) // TODO: Get from User model
                .build();
    }
}
