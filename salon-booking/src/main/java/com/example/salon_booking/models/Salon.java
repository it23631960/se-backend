package com.example.salon_booking.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "salons")
public class Salon {

    @Id
    private String id;

    private String name;
    private String description;
    private String bannerImage;
    private List<String> images = new ArrayList<>();  // multiple image URLs

    private List<String> reviews = new ArrayList<>(); // list of review IDs or text

    private String address;      // address as a string for simplicity

    private String phone;
    private String email;

    private List<String> services = new ArrayList<>(); // list of service names

    private String openTime;  // e.g. "09:00"
    private String closeTime; // e.g. "18:00"
    private boolean available;

    private String manager; // manager ID or name

    private List<String> bookings = new ArrayList<>(); // list of booking IDs

    private List<String> slotsBooked = new ArrayList<>(); // list of booked slot IDs or timestamps

    // ==================== RATING CACHE FIELDS ====================
    // These fields are updated automatically when reviews are added/modified
    // Improves performance by avoiding expensive aggregation queries
    
    /**
     * Cached average rating for this salon (0.0 to 5.0)
     * Automatically updated when reviews are added/modified
     * Null if no reviews exist yet
     */
    private Double averageRating;
    
    /**
     * Cached total number of reviews for this salon
     * Automatically updated when reviews are added/deleted
     * Default is 0
     */
    private Long totalReviews = 0L;

    // Getters and Setters

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerImage() {
        return bannerImage;
    }
    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getReviews() {
        return reviews;
    }
    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getServices() {
        return services;
    }
    public void setServices(List<String> services) {
        this.services = services;
    }

    public String getOpenTime() {
        return openTime;
    }
    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }
    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getManager() {
        return manager;
    }
    public void setManager(String manager) {
        this.manager = manager;
    }

    public List<String> getBookings() {
        return bookings;
    }
    public void setBookings(List<String> bookings) {
        this.bookings = bookings;
    }

    public List<String> getSlotsBooked() {
        return slotsBooked;
    }
    public void setSlotsBooked(List<String> slotsBooked) {
        this.slotsBooked = slotsBooked;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }
}
