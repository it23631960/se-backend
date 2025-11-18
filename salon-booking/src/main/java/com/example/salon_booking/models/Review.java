package com.example.salon_booking.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

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
 * Review entity representing customer reviews and ratings for salons.
 * 
 * <p>Supports both registered users and guest reviewers.</p>
 * 
 * <p><b>Indexes:</b></p>
 * <ul>
 *   <li>salon.id + reviewDate (DESC) - Fast sorted queries per salon</li>
 *   <li>salon.id + user.id (UNIQUE) - Prevent duplicate reviews</li>
 *   <li>user.id - User's review history</li>
 * </ul>
 * 
 * <p><b>Business Rules:</b></p>
 * <ul>
 *   <li>One review per user per salon</li>
 *   <li>Rating must be 1-5 stars</li>
 *   <li>Comment minimum 10 characters</li>
 *   <li>Can be marked as verified if from actual appointment</li>
 *   <li>Can be hidden by moderators (isVisible flag)</li>
 * </ul>
 * 
 * @author Salon Booking System
 * @version 2.0
 * @since 2025-10-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
@CompoundIndexes({
    @CompoundIndex(name = "salon_date_idx", def = "{'salon': 1, 'reviewDate': -1}"),
    @CompoundIndex(name = "salon_user_unique_idx", def = "{'salon': 1, 'user': 1}", unique = true, sparse = true)
})
public class Review {
    
    /**
     * Unique identifier for the review
     */
    @Id
    private String id;
    
    /**
     * Reference to the salon being reviewed
     * Required field - every review must be associated with a salon
     */
    @DBRef
    @NotNull(message = "Salon is required")
    @Indexed
    private Salon salon;
    
    /**
     * Reference to the registered user who wrote the review
     * Nullable - allows guest reviews (when user is not logged in)
     * If present, reviewerName and reviewerEmail should match user's profile
     */
    @DBRef
    @Indexed
    private User user;
    
    /**
     * Name of the person who wrote the review
     * Required for both registered users and guests
     * For registered users, this should be populated from User.username
     */
    @NotBlank(message = "Reviewer name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String reviewerName;
    
    /**
     * Email of the person who wrote the review
     * Required for both registered users and guests
     * For registered users, this should be populated from User.email
     * Used for communication and spam prevention
     */
    @NotBlank(message = "Reviewer email is required")
    @Email(message = "Invalid email format")
    private String reviewerEmail;
    
    /**
     * Star rating given to the salon
     * Must be between 1 and 5 (inclusive)
     * Required field
     */
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    /**
     * Written review comment/feedback
     * Must be between 10 and 500 characters to ensure quality
     * Required field
     */
    @NotBlank(message = "Review comment is required")
    @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters")
    private String comment;
    
    /**
     * Date and time when the review was created
     * Automatically set when review is first created
     * Indexed for sorting (newest first)
     */
    @Builder.Default
    @Indexed
    private LocalDateTime reviewDate = LocalDateTime.now();
    
    /**
     * Date and time when the review was last modified
     * Updated whenever review is edited
     * Null if review has never been edited
     */
    private LocalDateTime lastModified;
    
    /**
     * Flag indicating if this review is from a verified appointment
     * True if review is linked to an actual completed appointment
     * Helps identify genuine customer feedback
     */
    @Builder.Default
    private Boolean isVerified = false;
    
    /**
     * Reference to the appointment this review is based on
     * Nullable - only present for verified reviews
     * Links review to actual service experience
     */
    @DBRef
    private Appointment appointment;
    
    /**
     * Flag indicating if review is visible to public
     * Used for moderation - false hides inappropriate/spam reviews
     * Default is true (publicly visible)
     */
    @Builder.Default
    @Indexed
    private Boolean isVisible = true;
    
    /**
     * Number of users who found this review helpful
     * Incremented when users click "Helpful" button
     * Used for sorting popular reviews
     */
    @Builder.Default
    private Integer helpfulCount = 0;
    
    /**
     * Number of times this review was reported as spam/inappropriate
     * Used by moderation system to flag problematic reviews
     * Reviews with high report count may be auto-hidden
     */
    @Builder.Default
    private Integer reportCount = 0;
    
    /**
     * Admin/moderator notes about this review
     * Used internally for moderation tracking
     * Not visible to public
     */
    private String moderatorNotes;
    
    /**
     * Salon owner's response to the review
     * Allows businesses to respond to customer feedback
     * Optional field
     */
    private String ownerResponse;
    
    /**
     * Date when owner response was added
     */
    private LocalDateTime ownerResponseDate;
    
    /**
     * Check if this review can be edited by the user
     * Reviews can be edited within 24 hours of creation
     * @return true if review is less than 24 hours old
     */
    public boolean isEditable() {
        if (reviewDate == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(reviewDate.plusHours(24));
    }
    
    /**
     * Check if this is a guest review (no registered user)
     * @return true if user is null
     */
    public boolean isGuestReview() {
        return user == null;
    }
    
    /**
     * Get display name for the reviewer
     * @return reviewer name or "Anonymous" if missing
     */
    public String getDisplayName() {
        return (reviewerName != null && !reviewerName.isEmpty()) 
            ? reviewerName 
            : "Anonymous";
    }
    
    /**
     * Increment helpful count
     */
    public void incrementHelpfulCount() {
        if (this.helpfulCount == null) {
            this.helpfulCount = 1;
        } else {
            this.helpfulCount = this.helpfulCount + 1;
        }
    }
    
    /**
     * Increment report count
     */
    public void incrementReportCount() {
        if (this.reportCount == null) {
            this.reportCount = 1;
        } else {
            this.reportCount = this.reportCount + 1;
        }
    }
}