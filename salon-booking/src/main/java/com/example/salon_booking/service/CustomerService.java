package com.example.salon_booking.service;

import com.example.salon_booking.dto.CustomerDTO;
import com.example.salon_booking.exception.CustomerNotFoundException;
import com.example.salon_booking.models.Customer;
import com.example.salon_booking.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing customer operations
 * Handles customer creation, retrieval, and updates
 * 
 * @author Salon Booking System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    /**
     * Create a new customer or return existing one by email
     * @param dto Customer data
     * @return Customer entity
     */
    @Transactional
    public Customer createOrGetCustomer(CustomerDTO dto) {
        log.info("Creating or getting customer with email: {}", dto.getEmail());
        
        // Check if customer already exists
        return customerRepository.findByEmail(dto.getEmail())
                .orElseGet(() -> {
                    log.info("Customer not found, creating new customer");
                    Customer newCustomer = Customer.builder()
                            .name(dto.getName())
                            .email(dto.getEmail())
                            .phone(dto.getPhone())
                            .preferredContact(dto.getPreferredContact())
                            .notes(dto.getNotes())
                            .createdAt(LocalDateTime.now())
                            .build();
                    
                    Customer saved = customerRepository.save(newCustomer);
                    log.info("Created new customer with ID: {}", saved.getId());
                    return saved;
                });
    }
    
    /**
     * Get customer by ID
     * @param id Customer ID
     * @return Customer entity
     * @throws CustomerNotFoundException if customer not found
     */
    public Customer getCustomerById(String id) {
        log.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
    }
    
    /**
     * Get customer by email
     * @param email Customer email
     * @return Customer entity
     * @throws CustomerNotFoundException if customer not found
     */
    public Customer getCustomerByEmail(String email) {
        log.info("Fetching customer with email: {}", email);
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));
    }
    
    /**
     * Get all customers
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll();
    }
    
    /**
     * Search customers by name
     * @param name Name to search for
     * @return List of matching customers
     */
    public List<Customer> searchCustomersByName(String name) {
        log.info("Searching customers by name: {}", name);
        return customerRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Update customer information
     * @param id Customer ID
     * @param dto Updated customer data
     * @return Updated customer
     * @throws CustomerNotFoundException if customer not found
     */
    @Transactional
    public Customer updateCustomer(String id, CustomerDTO dto) {
        log.info("Updating customer with ID: {}", id);
        
        Customer customer = getCustomerById(id);
        
        if (dto.getName() != null) {
            customer.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            customer.setPhone(dto.getPhone());
        }
        if (dto.getPreferredContact() != null) {
            customer.setPreferredContact(dto.getPreferredContact());
        }
        if (dto.getNotes() != null) {
            customer.setNotes(dto.getNotes());
        }
        
        Customer updated = customerRepository.save(customer);
        log.info("Updated customer: {}", updated.getId());
        return updated;
    }
    
    /**
     * Delete customer
     * @param id Customer ID
     * @throws CustomerNotFoundException if customer not found
     */
    @Transactional
    public void deleteCustomer(String id) {
        log.info("Deleting customer with ID: {}", id);
        
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
        
        customerRepository.deleteById(id);
        log.info("Deleted customer: {}", id);
    }
}
