package com.example.salon_booking.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Review responses.
 * 
 * <p>Used in GET endpoints to return review data to clients.</p>
 * 
 * <p>Contains all public-facing review information while hiding
 * sensitive fields like reportCount and moderatorNotes.</p>
 * 
 * @author Salon Booking System
 * @version 1.0
 * @since 2025-10-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    
    /**
     * Review ID
     */
    private String id;
    
    /**
     * ID of the salon being reviewed
     */
    private String salonId;
    
    /**
     * Name of the salon (populated for convenience)
     */
    private String salonName;
    
    /**
     * ID of the user who wrote the review
     * Null for guest reviews
     */
    private String userId;
    
    /**
     * Display name of the reviewer
     */
    private String reviewerName;
    
    /**
     * Star rating (1-5)
     */
    private Integer rating;
    
    /**
     * Review comment text
     */
    private String comment;
    
    /**
     * Date when review was created
     */
    private LocalDateTime reviewDate;
    
    /**
     * Date when review was last modified
     * Null if never edited
     */
    private LocalDateTime lastModified;
    
    /**
     * Whether this review is from a verified appointment
     */
    private Boolean isVerified;
    
    /**
     * Number of users who found this review helpful
     */
    private Integer helpfulCount;
    
    /**
     * Whether this is a guest review
     */
    private Boolean isGuestReview;
    
    /**
     * Whether review can still be edited (within 24 hours)
     */
    private Boolean isEditable;
    
    /**
     * Salon owner's response to the review
     */
    private String ownerResponse;
    
    /**
     * Date when owner response was added
     */
    private LocalDateTime ownerResponseDate;
    
    /**
     * User's avatar URL (if available)
     * Can be populated from User model if needed
     */
    private String userAvatar;
}
