package com.example.salon_booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new review.
 * 
 * <p>Used in POST /api/reviews endpoint to submit a new review.</p>
 * 
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *   <li>salonId: Required - ID of the salon being reviewed</li>
 *   <li>rating: Required, 1-5 stars</li>
 *   <li>comment: Required, 10-500 characters</li>
 *   <li>reviewerName: Required, 2-100 characters</li>
 *   <li>reviewerEmail: Required, valid email format</li>
 *   <li>userId: Optional - if user is logged in</li>
 *   <li>appointmentId: Optional - if review is from verified appointment</li>
 * </ul>
 * 
 * @author Salon Booking System
 * @version 1.0
 * @since 2025-10-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDTO {
    
    /**
     * ID of the salon being reviewed
     * Required field
     */
    @NotBlank(message = "Salon ID is required")
    private String salonId;
    
    /**
     * ID of the user submitting the review
     * Optional - only present if user is logged in
     * If null, treated as guest review
     */
    private String userId;
    
    /**
     * Name of the person writing the review
     * Required for both registered users and guests
     */
    @NotBlank(message = "Reviewer name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String reviewerName;
    
    /**
     * Email of the person writing the review
     * Required for both registered users and guests
     */
    @NotBlank(message = "Reviewer email is required")
    @Email(message = "Invalid email format")
    private String reviewerEmail;
    
    /**
     * Star rating (1-5)
     * Required field
     */
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    /**
     * Written review comment
     * Required, 10-500 characters
     */
    @NotBlank(message = "Review comment is required")
    @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters")
    private String comment;
    
    /**
     * ID of the appointment this review is based on
     * Optional - only present for verified reviews
     * If provided, review will be marked as verified
     */
    private String appointmentId;
}
