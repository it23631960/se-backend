package com.example.salon_booking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating an existing review.
 * 
 * <p>Used in PUT /api/reviews/{id} endpoint to update a review.</p>
 * 
 * <p><b>Update Rules:</b></p>
 * <ul>
 *   <li>Only rating and comment can be updated</li>
 *   <li>Cannot change reviewer information or salon</li>
 *   <li>Updates are only allowed within 24 hours of creation</li>
 *   <li>lastModified timestamp is automatically updated</li>
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
public class UpdateReviewDTO {
    
    /**
     * Updated star rating (1-5)
     * Required field
     */
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    /**
     * Updated review comment
     * Required, 10-500 characters
     */
    @NotNull(message = "Review comment is required")
    @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters")
    private String comment;
}
