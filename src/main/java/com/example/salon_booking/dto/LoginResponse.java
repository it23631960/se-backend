package com.example.salon_booking.dto;

public class LoginResponse {
    
    private String id;
    private String username;
    private String email;
    private String message;
    private boolean success;
    
    // Default constructor
    public LoginResponse() {}
    
    // Constructor for successful login
    public LoginResponse(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.success = true;
        this.message = "Login successful";
    }
    
    // Constructor for failed login
    public LoginResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}