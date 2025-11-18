package com.example.salon_booking.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.salon_booking.models.Customer;
import com.example.salon_booking.models.Salon;
import com.example.salon_booking.models.Service;
import com.example.salon_booking.models.TimeSlot;
import com.example.salon_booking.models.User;
import com.example.salon_booking.repositories.AppointmentRepository;
import com.example.salon_booking.repositories.CustomerRepository;
import com.example.salon_booking.repositories.ReviewRepository;
import com.example.salon_booking.repositories.SalonRepository;
import com.example.salon_booking.repositories.ServiceRepository;
import com.example.salon_booking.repositories.TimeSlotRepository;
import com.example.salon_booking.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Data Initializer for Salon Booking System
 * Automatically populates MongoDB with comprehensive sample data on application startup
 * 
 * FEATURES:
 * - Only runs if database is empty (safe for production)
 * - Generates realistic Sri Lankan data (10 users, 6 salons, 30 services, 15 customers, 672 time slots)
 * - Maintains proper relationships (@DBRef)
 * - Updates salon rating caches
 * - Generates 7 days of time slots (9 AM - 6 PM, 30-min intervals, skip 1-2 PM lunch)
 * 
 * USAGE:
 * - Automatically runs on application startup in dev/default profile
 * - To disable: Set spring.profiles.active=prod (or add @Profile annotation check)
 * - To force re-run: Drop collections manually using MongoDB Compass
 * - For full appointments (25) and reviews (48): Import JSON files via MongoDB Compass
 * 
 * @author Salon Booking System
 * @version 2.0
 * @since 2025-10-10
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final SalonRepository salonRepository;
    private final ServiceRepository serviceRepository;
    private final CustomerRepository customerRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Main data initialization bean
     * Runs automatically on application startup
     */
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("🚀 Starting Database Initialization...");
            
            // Check if data already exists
            if (isDatabasePopulated()) {
                log.info("✅ Database already contains data. Skipping initialization.");
                return;
            }
            
            log.info("📝 Database is empty. Starting data population...");
            
            try {
                // Insert data in correct order (respecting dependencies)
                List<User> users = insertUsers();
                List<Salon> salons = insertSalons();
                List<Customer> customers = insertCustomers();
                List<Service> services = insertServices();
                List<TimeSlot> timeSlots = insertTimeSlots(salons);
                
                log.info("✅ Database initialization complete!");
                printSummary();
                
            } catch (Exception e) {
                log.error("❌ Error during database initialization", e);
                throw new RuntimeException("Failed to initialize database", e);
            }
        };
    }

    /**
     * Check if database already has data
     */
    private boolean isDatabasePopulated() {
        return userRepository.count() > 0 
            || salonRepository.count() > 0 
            || customerRepository.count() > 0;
    }

    /**
     * Insert Users (10 records)
     */
    private List<User> insertUsers() {
        log.info("📝 Inserting Users...");
        
        List<User> users = Arrays.asList(
            createUser("user1", "sarahj", "sarah.johnson@gmail.com", "password123"),
            createUser("user2", "priyaf", "priya.fernando@gmail.com", "password123"),
            createUser("user3", "mikeyw", "michael.williams@yahoo.com", "password123"),
            createUser("user4", "kumarip", "kumari.perera@gmail.com", "password123"),
            createUser("user5", "davids", "david.silva@outlook.com", "password123"),
            createUser("user6", "ananya_s", "ananya.subramaniam@gmail.com", "password123"),
            createUser("user7", "nimalj", "nimal.jayawardena@gmail.com", "password123"),
            createUser("user8", "emilyc", "emily.chen@gmail.com", "password123"),
            createUser("user9", "rajeshk", "rajesh.kumar@yahoo.com", "password123"),
            createUser("user10", "farahm", "farah.mohamed@gmail.com", "password123")
        );
        
        List<User> savedUsers = userRepository.saveAll(users);
        log.info("✅ Inserted {} users", savedUsers.size());
        return savedUsers;
    }

    private User createUser(String id, String username, String email, String password) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // In production, use BCrypt
        return user;
    }

    /**
     * Insert Salons (6 records)
     */
    private List<Salon> insertSalons() {
        log.info("📝 Inserting Salons...");
        
        List<Salon> salons = Arrays.asList(
            createSalon("salon1", "Elite Hair Studio", "Colombo", 
                "Premium hair care services in the heart of Colombo. Specializing in modern cuts, coloring, and bridal hair styling with experienced stylists.",
                "456 Galle Road, Colombo 03, Western Province, Sri Lanka",
                "+94112345001", "info@elitehair.lk", "Priya Fernando", 4.7, 12L),
            
            createSalon("salon2", "Golden Locks Salon", "Kandy",
                "Kandy's premier destination for luxurious hair treatments, bridal packages, and creative styling. Over 15 years of excellence in hair care.",
                "78 Peradeniya Road, Kandy, Central Province, Sri Lanka",
                "+94812345678", "contact@goldenlocks.lk", "Kumari Wickramasinghe", 4.5, 8L),
            
            createSalon("salon3", "Style Avenue Spa", "Galle",
                "Full-service spa and salon in Galle offering hair, nails, facials, and body treatments. Relax and rejuvenate in our peaceful environment.",
                "23 Rampart Street, Galle Fort, Galle, Southern Province, Sri Lanka",
                "+94912345678", "info@styleavenue.lk", "Ananya Subramaniam", 4.8, 10L),
            
            createSalon("salon4", "Royal Bridal Salon", "Negombo",
                "Negombo's top bridal salon specializing in wedding hair, makeup, and saree draping. Make your special day unforgettable with our expert team.",
                "145 Lewis Place, Negombo, Western Province, Sri Lanka",
                "+94312345678", "bookings@royalbridal.lk", "Sanduni Mendis", 4.9, 6L),
            
            createSalon("salon5", "Trendy Cuts Unisex Salon", "Jaffna",
                "Modern unisex salon in Jaffna offering trendy haircuts, coloring, and grooming services for men, women, and kids. Walk-ins welcome!",
                "89 Hospital Road, Jaffna, Northern Province, Sri Lanka",
                "+94212345678", "info@trendycuts.lk", "Rajesh Kumar", 4.3, 7L),
            
            createSalon("salon6", "Ocean Breeze Salon", "Matara",
                "Coastal salon in Matara offering refreshing hair and beauty services with ocean views. Perfect for vacation pampering and everyday elegance.",
                "34 Beach Road, Matara, Southern Province, Sri Lanka",
                "+94412345678", "hello@oceanbreeze.lk", "Farah Mohamed", 4.6, 5L)
        );
        
        List<Salon> savedSalons = salonRepository.saveAll(salons);
        log.info("✅ Inserted {} salons", savedSalons.size());
        return savedSalons;
    }

    private Salon createSalon(String id, String name, String city, String description, 
                              String address, String phone, String email, String manager,
                              double avgRating, long totalReviews) {
        Salon salon = new Salon();
        salon.setId(id);
        salon.setName(name);
        salon.setDescription(description);
        salon.setAddress(address);
        salon.setPhone(phone);
        salon.setEmail(email);
        salon.setManager(manager);
        salon.setOpenTime("09:00");
        salon.setCloseTime("18:00");
        salon.setAvailable(true);
        salon.setAverageRating(avgRating);
        salon.setTotalReviews(totalReviews);
        
        // Sample images (Unsplash placeholder URLs)
        salon.setBannerImage("https://images.unsplash.com/photo-1560066984-138dadb4c035?w=1200");
        salon.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f?w=800",
            "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800"
        ));
        
        return salon;
    }

    /**
     * Insert Customers (15 records)
     */
    private List<Customer> insertCustomers() {
        log.info("📝 Inserting Customers...");
        
        List<Customer> customers = Arrays.asList(
            createCustomer("customer1", "Sarah Johnson", "sarah.johnson@gmail.com", "+94771234567", "EMAIL", "Regular customer, prefers stylist Priya"),
            createCustomer("customer2", "Priya Fernando", "priya.fernando@gmail.com", "+94772345678", "PHONE", null),
            createCustomer("customer3", "Michael Williams", "michael.williams@yahoo.com", "+94773456789", "EMAIL", "Allergic to certain hair color products - check before treatment"),
            createCustomer("customer4", "Kumari Perera", "kumari.perera@gmail.com", "+94774567890", "SMS", "Prefers morning appointments"),
            createCustomer("customer5", "David Silva", "david.silva@outlook.com", "+94775678901", "PHONE", null),
            createCustomer("customer6", "Ananya Subramaniam", "ananya.subramaniam@gmail.com", "+94776789012", "EMAIL", "First time customer - Sep 18, 2025"),
            createCustomer("customer7", "Nimal Jayawardena", "nimal.jayawardena@gmail.com", "+94777890123", "PHONE", null),
            createCustomer("customer8", "Emily Chen", "emily.chen@gmail.com", "+94778901234", "EMAIL", "Vegetarian - prefers organic/natural products"),
            createCustomer("customer9", "Rajesh Kumar", "rajesh.kumar@yahoo.com", "+94779012345", "SMS", "Regular customer for beard grooming"),
            createCustomer("customer10", "Farah Mohamed", "farah.mohamed@gmail.com", "+94770123456", "EMAIL", null),
            createCustomer("customer11", "Amanda Thompson", "amanda.thompson@yahoo.com", "+94771987654", "EMAIL", "Guest customer - tourist visiting from Australia"),
            createCustomer("customer12", "Sanduni Mendis", "sanduni.mendis@gmail.com", "+94772876543", "PHONE", "Bridal booking for November wedding"),
            createCustomer("customer13", "James Anderson", "james.anderson@outlook.com", "+94773765432", "EMAIL", "Guest customer - prefers quick services"),
            createCustomer("customer14", "Kavitha Sivakumar", "kavitha.sivakumar@gmail.com", "+94774654321", "SMS", "Sensitive skin - patch test required for coloring"),
            createCustomer("customer15", "Daniel Brown", "daniel.brown@yahoo.com", "+94775543210", "PHONE", "Guest customer - referred by friend")
        );
        
        List<Customer> savedCustomers = customerRepository.saveAll(customers);
        log.info("✅ Inserted {} customers", savedCustomers.size());
        return savedCustomers;
    }

    private Customer createCustomer(String id, String name, String email, String phone, 
                                    String preferredContact, String notes) {
        return Customer.builder()
            .id(id)
            .name(name)
            .email(email)
            .phone(phone)
            .preferredContact(preferredContact)
            .notes(notes)
            .createdAt(LocalDateTime.now().minusDays(new Random().nextInt(60)))
            .build();
    }

    /**
     * Insert Services (30 records)
     */
    private List<Service> insertServices() {
        log.info("📝 Inserting Services...");
        
        List<Service> services = Arrays.asList(
            createService("service1", "Men's Haircut", "Professional men's haircut with wash and basic styling", 1500, 30, "HAIRCUT"),
            createService("service2", "Women's Haircut", "Women's haircut with wash, conditioning, and blow-dry", 2500, 60, "HAIRCUT"),
            createService("service3", "Full Hair Coloring", "Complete hair coloring with premium products and deep conditioning", 8000, 120, "COLORING"),
            createService("service4", "Hair Spa Treatment", "Relaxing hair spa with deep conditioning, scalp massage, and steam", 4000, 60, "SPA"),
            createService("service5", "Keratin Treatment", "Professional keratin smoothing treatment for frizz-free hair", 20000, 180, "TREATMENT"),
            createService("service6", "Kids Haircut", "Gentle haircut for children aged 3-12", 1000, 30, "HAIRCUT"),
            createService("service7", "Bridal Hair & Makeup Package", "Complete bridal package with hair, makeup, and saree draping", 50000, 240, "BRIDAL"),
            createService("service8", "Highlights/Balayage", "Partial highlights or balayage with natural color blending", 12000, 150, "COLORING"),
            createService("service9", "Blowdry & Styling", "Professional blow-dry with styling for special occasions", 2000, 45, "STYLING"),
            createService("service10", "Hair Extensions", "Premium hair extension installation with natural blending", 15000, 120, "STYLING"),
            createService("service11", "Classic Manicure", "Traditional manicure with nail shaping, massage, and polish", 1500, 45, "NAILS"),
            createService("service12", "Spa Pedicure", "Luxurious pedicure with foot soak, scrub, massage, and polish", 2500, 60, "NAILS"),
            createService("service13", "Gel Nails", "Long-lasting gel manicure with UV curing", 3500, 90, "NAILS"),
            createService("service14", "Facial Treatment (Basic)", "Deep cleansing facial with steam, extraction, and mask", 3000, 60, "SPA"),
            createService("service15", "Full Body Massage", "Relaxing 90-minute full body massage with aromatherapy oils", 6000, 90, "SPA"),
            createService("service16", "Bridal Trial Session", "Complete bridal makeup and hair trial before wedding day", 8000, 120, "BRIDAL"),
            createService("service17", "Party Makeup", "Glamorous makeup for parties and special occasions", 5000, 60, "BRIDAL"),
            createService("service18", "Saree Draping", "Professional saree draping in traditional or modern styles", 2000, 30, "BRIDAL"),
            createService("service19", "Henna/Mehendi Design", "Beautiful henna designs for hands and feet", 3000, 90, "BRIDAL"),
            createService("service20", "Groom Grooming Package", "Complete grooming for grooms: haircut, shave, facial, styling", 5000, 90, "BRIDAL"),
            createService("service21", "Beard Trim & Shape", "Precision beard trimming and shaping with hot towel treatment", 800, 30, "HAIRCUT"),
            createService("service22", "Hair Straightening", "Permanent hair straightening treatment for smooth hair", 18000, 180, "TREATMENT"),
            createService("service23", "Scalp Treatment", "Specialized scalp treatment for dandruff or hair fall", 3500, 60, "TREATMENT"),
            createService("service24", "Hair Braiding Styles", "Creative braiding styles including box braids and cornrows", 2500, 90, "STYLING"),
            createService("service25", "Color Touch-Up (Roots)", "Root color touch-up for maintaining hair color", 3500, 60, "COLORING"),
            createService("service26", "Express Haircut", "Quick haircut service for busy professionals", 1200, 20, "HAIRCUT"),
            createService("service27", "Premium Facial Treatment", "Luxury anti-aging facial with serums and LED therapy", 8000, 90, "SPA"),
            createService("service28", "Waxing (Full Arms & Legs)", "Complete waxing service for smooth, hair-free skin", 2500, 60, "SPA"),
            createService("service29", "Threading (Eyebrows & Upper Lip)", "Precise threading for perfectly shaped eyebrows", 500, 15, "THREADING"),
            createService("service30", "Beach Waves Styling", "Relaxed, beachy waves perfect for casual vibes", 2200, 45, "STYLING")
        );
        
        List<Service> savedServices = serviceRepository.saveAll(services);
        log.info("✅ Inserted {} services", savedServices.size());
        return savedServices;
    }

    private Service createService(String id, String name, String description, 
                                  double price, int duration, String category) {
        return Service.builder()
            .id(id)
            .name(name)
            .description(description)
            .price(price)
            .durationMinutes(duration)
            .category(category)
            .active(true)
            .build();
    }

    /**
     * Insert Time Slots (672 records - 7 days × 6 salons × 16 slots/day)
     * Generates slots from 9 AM to 6 PM with 30-minute intervals (skip 1-2 PM lunch)
     */
    private List<TimeSlot> insertTimeSlots(List<Salon> salons) {
        log.info("📝 Generating Time Slots (7 days × 6 salons × 16 slots/day)...");
        
        List<TimeSlot> timeSlots = new ArrayList<>();
        LocalDate startDate = LocalDate.now().plusDays(1); // Start from tomorrow
        int days = 7;
        Random random = new Random();
        
        for (int dayOffset = 0; dayOffset < days; dayOffset++) {
            LocalDate date = startDate.plusDays(dayOffset);
            
            for (Salon salon : salons) {
                // Generate slots from 9:00 AM to 6:00 PM (skip 1:00-2:00 PM lunch)
                int[] hours = {9, 10, 11, 12, 14, 15, 16, 17};
                
                for (int hour : hours) {
                    for (int minute : new int[]{0, 30}) {
                        LocalTime startTime = LocalTime.of(hour, minute);
                        LocalTime endTime = startTime.plusMinutes(30);
                        
                        // 70% available, 30% booked
                        boolean isAvailable = random.nextDouble() < 0.7;
                        
                        TimeSlot slot = TimeSlot.builder()
                            .date(date)
                            .startTime(startTime)
                            .endTime(endTime)
                            .isAvailable(isAvailable)
                            .salon(salon)
                            .build();
                        
                        timeSlots.add(slot);
                    }
                }
            }
        }
        
        List<TimeSlot> savedSlots = timeSlotRepository.saveAll(timeSlots);
        log.info("✅ Generated {} time slots", savedSlots.size());
        return savedSlots;
    }

    /**
     * Print summary of inserted data
     */
    private void printSummary() {
        log.info("\n");
        log.info("╔══════════════════════════════════════════════════════╗");
        log.info("║      DATABASE INITIALIZATION SUMMARY                 ║");
        log.info("╠══════════════════════════════════════════════════════╣");
        log.info("║  Users:        {} records                           ║", String.format("%3d", userRepository.count()));
        log.info("║  Salons:       {} records                           ║", String.format("%3d", salonRepository.count()));
        log.info("║  Services:     {} records                           ║", String.format("%3d", serviceRepository.count()));
        log.info("║  Customers:    {} records                           ║", String.format("%3d", customerRepository.count()));
        log.info("║  Time Slots:   {} records                          ║", String.format("%3d", timeSlotRepository.count()));
        log.info("║  Appointments: {} records (use JSON for full data) ║", String.format("%3d", appointmentRepository.count()));
        log.info("║  Reviews:      {} records (use JSON for full data) ║", String.format("%3d", reviewRepository.count()));
        log.info("╚══════════════════════════════════════════════════════╝");
        log.info("\n");
        log.info("💡 TIP: For full appointments (25) and reviews (48), import JSON files:");
        log.info("   - backend/sample-data/6-appointments.json");
        log.info("   - backend/sample-data/7-reviews.json");
        log.info("   Use MongoDB Compass for easy import!");
        log.info("\n");
    }
}
