package com.example.salon_booking.models;

import org.apache.catalina.Manager;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;


import java.util.List;

@Document(collection = "salons")
public class Salon {

    @Id
    private String id;

    private String name;
    private String description;
    private String category; // HAIR, NAIL, BRIDAL, BARBER
    private String bannerImage;
    private List<String> images;  // multiple image URLs

    private List<Review> reviews; // embedded reviews

    private Address address;      // embedded address

    private String phone;
    private String email;

    private List<Service> services; // list of services with price + description

    private String openTime;  // e.g. "09:00"
    private String closeTime; // e.g. "18:00"
    private boolean available;

    @DBRef
    private Manager manager; // linked to Manager model

    @DBRef
    private List<Booking> bookings; // linked bookings

    private List<String> slotsBooked; // list of booked slot IDs or timestamps

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

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
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

    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
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

    public List<Service> getServices() {
        return services;
    }
    public void setServices(List<Service> services) {
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

    public Manager getManager() {
        return manager;
    }
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<String> getSlotsBooked() {
        return slotsBooked;
    }
    public void setSlotsBooked(List<String> slotsBooked) {
        this.slotsBooked = slotsBooked;
    }
}

@Document(collection = "bookings")
class Booking {
    @Id
    private String id;
    // add fields as needed (e.g., userId, serviceId, timeSlot, status, etc.)
}
