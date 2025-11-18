package com.example.salon_booking.config;

import com.example.salon_booking.models.Salon;
import com.example.salon_booking.repositories.SalonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SampleDataInitializer {

    @Bean
    CommandLineRunner initDatabase(SalonRepository salonRepository) {
        return args -> {
            // Only initialize if database is empty
            if (salonRepository.count() == 0) {
                System.out.println("Initializing sample salon data...");
                
                // Note: This initializer creates basic salons without complex embedded objects
                // You'll need to add Address, Service, Manager objects separately if needed
                
                // Hair Salon 1
                Salon hairSalon1 = createBasicSalon(
                    "Glamour Hair Studio",
                    "Premium hair styling and treatments",
                    "HAIR",
                    "123 Main St, New York, NY 10001",
                    "(555) 123-4567",
                    "info@glamourhair.com"
                );
                
                // Hair Salon 2
                Salon hairSalon2 = createBasicSalon(
                    "Elegance Hair & Beauty",
                    "Modern hair care and beauty services",
                    "HAIR",
                    "456 Broadway Ave, New York, NY 10002",
                    "(555) 234-5678",
                    "contact@elegancehair.com"
                );
                
                // Nail Salon 1
                Salon nailSalon1 = createBasicSalon(
                    "Perfect Nails Spa",
                    "Professional nail care and spa services",
                    "NAIL",
                    "789 Park Ave, New York, NY 10003",
                    "(555) 345-6789",
                    "info@perfectnails.com"
                );
                
                // Nail Salon 2
                Salon nailSalon2 = createBasicSalon(
                    "Luxe Nail Lounge",
                    "Luxury nail and beauty treatments",
                    "NAIL",
                    "321 5th Ave, New York, NY 10004",
                    "(555) 456-7890",
                    "hello@luxenail.com"
                );
                
                // Bridal Salon 1
                Salon bridalSalon1 = createBasicSalon(
                    "Blissful Bridal Studio",
                    "Complete bridal beauty packages",
                    "BRIDAL",
                    "654 Madison Ave, New York, NY 10005",
                    "(555) 567-8901",
                    "bookings@blissfulbridal.com"
                );
                
                // Bridal Salon 2
                Salon bridalSalon2 = createBasicSalon(
                    "Royal Wedding Salon",
                    "Premium bridal beauty and styling",
                    "BRIDAL",
                    "987 Lexington Ave, New York, NY 10006",
                    "(555) 678-9012",
                    "info@royalwedding.com"
                );
                
                // Barber Shop 1
                Salon barberShop1 = createBasicSalon(
                    "Classic Cuts Barbershop",
                    "Traditional and modern men's grooming",
                    "BARBER",
                    "147 Amsterdam Ave, New York, NY 10007",
                    "(555) 789-0123",
                    "cuts@classiccuts.com"
                );
                
                // Barber Shop 2
                Salon barberShop2 = createBasicSalon(
                    "The Gentleman's Parlor",
                    "Premium men's grooming and styling",
                    "BARBER",
                    "258 Columbus Ave, New York, NY 10008",
                    "(555) 890-1234",
                    "info@gentlemansparlor.com"
                );
                
                // Save all salons
                List<Salon> salons = Arrays.asList(
                    hairSalon1, hairSalon2,
                    nailSalon1, nailSalon2,
                    bridalSalon1, bridalSalon2,
                    barberShop1, barberShop2
                );
                
                salonRepository.saveAll(salons);
                System.out.println("Successfully initialized " + salons.size() + " sample salons!");
                System.out.println("Categories: 2 HAIR, 2 NAIL, 2 BRIDAL, 2 BARBER");
            } else {
                System.out.println("Database already contains data. Skipping initialization.");
            }
        };
    }
    
    private Salon createBasicSalon(String name, String description, String category,
                                   String address, String phone, String email) {
        Salon salon = new Salon();
        salon.setName(name);
        salon.setDescription(description);
        salon.setCategory(category);
        salon.setBannerImage("/images/placeholder.jpg");
        salon.setImages(Arrays.asList("/images/placeholder.jpg"));
        // Leave address, services as null for now - frontend will handle
        salon.setPhone(phone);
        salon.setEmail(email);
        salon.setOpenTime("09:00");
        salon.setCloseTime("19:00");
        salon.setAvailable(true);
        return salon;
    }
}
