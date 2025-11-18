package com.example.salon_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for rating statistics and distribution.
 * 
 * <p>Provides aggregated rating data for a salon, including average rating
 * and breakdown by star level.</p>
 * 
 * <p>Used in GET /api/reviews/salon/{salonId}/statistics endpoint.</p>
 * 
 * @author Salon Booking System
 * @version 1.0
 * @since 2025-10-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatisticsDTO {
    
    /**
     * ID of the salon these statistics are for
     */
    private String salonId;
    
    /**
     * Average rating across all reviews
     * Calculated from all visible reviews
     * Example: 4.8
     */
    private Double averageRating;
    
    /**
     * Total number of reviews
     * Only counts visible reviews
     */
    private Long totalReviews;
    
    /**
     * Number of 5-star reviews
     */
    @Builder.Default
    private Long fiveStars = 0L;
    
    /**
     * Number of 4-star reviews
     */
    @Builder.Default
    private Long fourStars = 0L;
    
    /**
     * Number of 3-star reviews
     */
    @Builder.Default
    private Long threeStars = 0L;
    
    /**
     * Number of 2-star reviews
     */
    @Builder.Default
    private Long twoStars = 0L;
    
    /**
     * Number of 1-star reviews
     */
    @Builder.Default
    private Long oneStar = 0L;
    
    /**
     * Percentage of 5-star reviews
     */
    public Double getFiveStarPercentage() {
        return totalReviews > 0 ? (fiveStars * 100.0) / totalReviews : 0.0;
    }
    
    /**
     * Percentage of 4-star reviews
     */
    public Double getFourStarPercentage() {
        return totalReviews > 0 ? (fourStars * 100.0) / totalReviews : 0.0;
    }
    
    /**
     * Percentage of 3-star reviews
     */
    public Double getThreeStarPercentage() {
        return totalReviews > 0 ? (threeStars * 100.0) / totalReviews : 0.0;
    }
    
    /**
     * Percentage of 2-star reviews
     */
    public Double getTwoStarPercentage() {
        return totalReviews > 0 ? (twoStars * 100.0) / totalReviews : 0.0;
    }
    
    /**
     * Percentage of 1-star reviews
     */
    public Double getOneStarPercentage() {
        return totalReviews > 0 ? (oneStar * 100.0) / totalReviews : 0.0;
    }
}
