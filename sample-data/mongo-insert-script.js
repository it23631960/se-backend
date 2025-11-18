// MongoDB Insert Script for Salon Booking Database
// Run this script using: mongosh < mongo-insert-script.js
// Or load in MongoDB Shell: load('mongo-insert-script.js')

// ===================================================================
// DATABASE SETUP
// ===================================================================

// Switch to salon_booking database (creates if doesn't exist)
use salon_booking;

print("🚀 Starting Salon Booking Database Population...\n");

// ===================================================================
// 1. INSERT USERS (10 records)
// ===================================================================

print("📝 Step 1: Inserting Users...");

db.users.insertMany([
  { _id: "user1", username: "sarahj", email: "sarah.johnson@gmail.com", password: "password123" },
  { _id: "user2", username: "priyaf", email: "priya.fernando@gmail.com", password: "password123" },
  { _id: "user3", username: "mikeyw", email: "michael.williams@yahoo.com", password: "password123" },
  { _id: "user4", username: "kumarip", email: "kumari.perera@gmail.com", password: "password123" },
  { _id: "user5", username: "davids", email: "david.silva@outlook.com", password: "password123" },
  { _id: "user6", username: "ananya_s", email: "ananya.subramaniam@gmail.com", password: "password123" },
  { _id: "user7", username: "nimalj", email: "nimal.jayawardena@gmail.com", password: "password123" },
  { _id: "user8", username: "emilyc", email: "emily.chen@gmail.com", password: "password123" },
  { _id: "user9", username: "rajeshk", email: "rajesh.kumar@yahoo.com", password: "password123" },
  { _id: "user10", username: "farahm", email: "farah.mohamed@gmail.com", password: "password123" }
]);

print("✅ Inserted " + db.users.countDocuments() + " users\n");

// ===================================================================
// 2. INSERT SALONS (6 records)
// ===================================================================

print("📝 Step 2: Inserting Salons...");

db.salons.insertMany([
  {
    _id: "salon1",
    name: "Elite Hair Studio",
    description: "Premium hair care services in the heart of Colombo. Specializing in modern cuts, coloring, and bridal hair styling with experienced stylists.",
    bannerImage: "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=1200",
    images: [
      "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f?w=800",
      "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800",
      "https://images.unsplash.com/photo-1562322140-8baeececf3df?w=800"
    ],
    reviews: [],
    address: "456 Galle Road, Colombo 03, Western Province, Sri Lanka",
    phone: "+94112345001",
    email: "info@elitehair.lk",
    services: [],
    openTime: "09:00",
    closeTime: "18:00",
    available: true,
    manager: "Priya Fernando",
    bookings: [],
    slotsBooked: [],
    averageRating: 4.7,
    totalReviews: NumberLong(12)
  },
  {
    _id: "salon2",
    name: "Golden Locks Salon",
    description: "Kandy's premier destination for luxurious hair treatments, bridal packages, and creative styling. Over 15 years of excellence in hair care.",
    bannerImage: "https://images.unsplash.com/photo-1562322140-8baeececf3df?w=1200",
    images: [
      "https://images.unsplash.com/photo-1580618672591-eb180b1a973f?w=800",
      "https://images.unsplash.com/photo-1633681926022-84c23e8cb2d6?w=800"
    ],
    reviews: [],
    address: "78 Peradeniya Road, Kandy, Central Province, Sri Lanka",
    phone: "+94812345678",
    email: "contact@goldenlocks.lk",
    services: [],
    openTime: "09:00",
    closeTime: "18:00",
    available: true,
    manager: "Kumari Wickramasinghe",
    bookings: [],
    slotsBooked: [],
    averageRating: 4.5,
    totalReviews: NumberLong(8)
  },
  {
    _id: "salon3",
    name: "Style Avenue Spa",
    description: "Full-service spa and salon in Galle offering hair, nails, facials, and body treatments. Relax and rejuvenate in our peaceful environment.",
    bannerImage: "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?w=1200",
    images: [
      "https://images.unsplash.com/photo-1519415510236-718bdfcd89c8?w=800",
      "https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=800"
    ],
    reviews: [],
    address: "23 Rampart Street, Galle Fort, Galle, Southern Province, Sri Lanka",
    phone: "+94912345678",
    email: "info@styleavenue.lk",
    services: [],
    openTime: "09:00",
    closeTime: "18:00",
    available: true,
    manager: "Ananya Subramaniam",
    bookings: [],
    slotsBooked: [],
    averageRating: 4.8,
    totalReviews: NumberLong(10)
  },
  {
    _id: "salon4",
    name: "Royal Bridal Salon",
    description: "Negombo's top bridal salon specializing in wedding hair, makeup, and saree draping. Make your special day unforgettable with our expert team.",
    bannerImage: "https://images.unsplash.com/photo-1487412947147-5cebf100ffc2?w=1200",
    images: [
      "https://images.unsplash.com/photo-1595475207225-428b62bda831?w=800",
      "https://images.unsplash.com/photo-1516975080664-ed2fc6a32937?w=800"
    ],
    reviews: [],
    address: "145 Lewis Place, Negombo, Western Province, Sri Lanka",
    phone: "+94312345678",
    email: "bookings@royalbridal.lk",
    services: [],
    openTime: "09:00",
    closeTime: "18:00",
    available: true,
    manager: "Sanduni Mendis",
    bookings: [],
    slotsBooked: [],
    averageRating: 4.9,
    totalReviews: NumberLong(6)
  },
  {
    _id: "salon5",
    name: "Trendy Cuts Unisex Salon",
    description: "Modern unisex salon in Jaffna offering trendy haircuts, coloring, and grooming services for men, women, and kids. Walk-ins welcome!",
    bannerImage: "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=1200",
    images: [
      "https://images.unsplash.com/photo-1562004760-acb5f7f99a84?w=800",
      "https://images.unsplash.com/photo-1620331311520-246422fd82f9?w=800"
    ],
    reviews: [],
    address: "89 Hospital Road, Jaffna, Northern Province, Sri Lanka",
    phone: "+94212345678",
    email: "info@trendycuts.lk",
    services: [],
    openTime: "09:00",
    closeTime: "18:00",
    available: true,
    manager: "Rajesh Kumar",
    bookings: [],
    slotsBooked: [],
    averageRating: 4.3,
    totalReviews: NumberLong(7)
  },
  {
    _id: "salon6",
    name: "Ocean Breeze Salon",
    description: "Coastal salon in Matara offering refreshing hair and beauty services with ocean views. Perfect for vacation pampering and everyday elegance.",
    bannerImage: "https://images.unsplash.com/photo-1522337094846-8a818192de1f?w=1200",
    images: [
      "https://images.unsplash.com/photo-1560869713-7d0a29430803?w=800",
      "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=800"
    ],
    reviews: [],
    address: "34 Beach Road, Matara, Southern Province, Sri Lanka",
    phone: "+94412345678",
    email: "hello@oceanbreeze.lk",
    services: [],
    openTime: "09:00",
    closeTime: "18:00",
    available: true,
    manager: "Farah Mohamed",
    bookings: [],
    slotsBooked: [],
    averageRating: 4.6,
    totalReviews: NumberLong(5)
  }
]);

print("✅ Inserted " + db.salons.countDocuments() + " salons\n");

// ===================================================================
// 3. INSERT CUSTOMERS (15 records)
// ===================================================================

print("📝 Step 3: Inserting Customers...");

db.customers.insertMany([
  {
    _id: "customer1",
    name: "Sarah Johnson",
    email: "sarah.johnson@gmail.com",
    phone: "+94771234567",
    createdAt: ISODate("2025-09-15T10:30:00Z"),
    preferredContact: "EMAIL",
    notes: "Regular customer, prefers stylist Priya"
  },
  {
    _id: "customer2",
    name: "Priya Fernando",
    email: "priya.fernando@gmail.com",
    phone: "+94772345678",
    createdAt: ISODate("2025-08-20T14:15:00Z"),
    preferredContact: "PHONE",
    notes: null
  },
  {
    _id: "customer3",
    name: "Michael Williams",
    email: "michael.williams@yahoo.com",
    phone: "+94773456789",
    createdAt: ISODate("2025-09-01T09:00:00Z"),
    preferredContact: "EMAIL",
    notes: "Allergic to certain hair color products - check before treatment"
  },
  {
    _id: "customer4",
    name: "Kumari Perera",
    email: "kumari.perera@gmail.com",
    phone: "+94774567890",
    createdAt: ISODate("2025-07-10T16:45:00Z"),
    preferredContact: "SMS",
    notes: "Prefers morning appointments"
  },
  {
    _id: "customer5",
    name: "David Silva",
    email: "david.silva@outlook.com",
    phone: "+94775678901",
    createdAt: ISODate("2025-08-05T11:20:00Z"),
    preferredContact: "PHONE",
    notes: null
  },
  {
    _id: "customer6",
    name: "Ananya Subramaniam",
    email: "ananya.subramaniam@gmail.com",
    phone: "+94776789012",
    createdAt: ISODate("2025-09-18T13:30:00Z"),
    preferredContact: "EMAIL",
    notes: "First time customer - Sep 18, 2025"
  },
  {
    _id: "customer7",
    name: "Nimal Jayawardena",
    email: "nimal.jayawardena@gmail.com",
    phone: "+94777890123",
    createdAt: ISODate("2025-08-12T10:00:00Z"),
    preferredContact: "PHONE",
    notes: null
  },
  {
    _id: "customer8",
    name: "Emily Chen",
    email: "emily.chen@gmail.com",
    phone: "+94778901234",
    createdAt: ISODate("2025-09-22T15:10:00Z"),
    preferredContact: "EMAIL",
    notes: "Vegetarian - prefers organic/natural products"
  },
  {
    _id: "customer9",
    name: "Rajesh Kumar",
    email: "rajesh.kumar@yahoo.com",
    phone: "+94779012345",
    createdAt: ISODate("2025-07-28T08:45:00Z"),
    preferredContact: "SMS",
    notes: "Regular customer for beard grooming"
  },
  {
    _id: "customer10",
    name: "Farah Mohamed",
    email: "farah.mohamed@gmail.com",
    phone: "+94770123456",
    createdAt: ISODate("2025-09-05T12:00:00Z"),
    preferredContact: "EMAIL",
    notes: null
  },
  {
    _id: "customer11",
    name: "Amanda Thompson",
    email: "amanda.thompson@yahoo.com",
    phone: "+94771987654",
    createdAt: ISODate("2025-10-01T14:20:00Z"),
    preferredContact: "EMAIL",
    notes: "Guest customer - tourist visiting from Australia"
  },
  {
    _id: "customer12",
    name: "Sanduni Mendis",
    email: "sanduni.mendis@gmail.com",
    phone: "+94772876543",
    createdAt: ISODate("2025-09-28T10:15:00Z"),
    preferredContact: "PHONE",
    notes: "Bridal booking for November wedding"
  },
  {
    _id: "customer13",
    name: "James Anderson",
    email: "james.anderson@outlook.com",
    phone: "+94773765432",
    createdAt: ISODate("2025-10-03T09:30:00Z"),
    preferredContact: "EMAIL",
    notes: "Guest customer - prefers quick services"
  },
  {
    _id: "customer14",
    name: "Kavitha Sivakumar",
    email: "kavitha.sivakumar@gmail.com",
    phone: "+94774654321",
    createdAt: ISODate("2025-09-10T16:00:00Z"),
    preferredContact: "SMS",
    notes: "Sensitive skin - patch test required for coloring"
  },
  {
    _id: "customer15",
    name: "Daniel Brown",
    email: "daniel.brown@yahoo.com",
    phone: "+94775543210",
    createdAt: ISODate("2025-10-05T11:45:00Z"),
    preferredContact: "PHONE",
    notes: "Guest customer - referred by friend"
  }
]);

print("✅ Inserted " + db.customers.countDocuments() + " customers\n");

// ===================================================================
// 4. INSERT SERVICES (30 records - 5 per salon)
// ===================================================================

print("📝 Step 4: Inserting Services...");

// NOTE: Services are NOT linked to salons via @DBRef in the current model
// They are global services that can be filtered by context
// If you want to link services to specific salons, add a "salon" DBRef field

db.services.insertMany([
  // Salon 1 services (service1-5)
  { _id: "service1", name: "Men's Haircut", description: "Professional men's haircut with wash and basic styling. Includes consultation with stylist.", price: 1500, durationMinutes: 30, category: "HAIRCUT", active: true },
  { _id: "service2", name: "Women's Haircut", description: "Women's haircut with wash, conditioning treatment, and blow-dry styling.", price: 2500, durationMinutes: 60, category: "HAIRCUT", active: true },
  { _id: "service3", name: "Full Hair Coloring", description: "Complete hair coloring service with premium color products. Includes toner and deep conditioning.", price: 8000, durationMinutes: 120, category: "COLORING", active: true },
  { _id: "service4", name: "Hair Spa Treatment", description: "Relaxing hair spa with deep conditioning, scalp massage, and steam treatment.", price: 4000, durationMinutes: 60, category: "SPA", active: true },
  { _id: "service5", name: "Keratin Treatment", description: "Professional keratin smoothing treatment for frizz-free, silky hair lasting 3-4 months.", price: 20000, durationMinutes: 180, category: "TREATMENT", active: true },
  
  // Salon 2 services (service6-10)
  { _id: "service6", name: "Kids Haircut", description: "Gentle haircut for children aged 3-12. Patient stylists, fun environment.", price: 1000, durationMinutes: 30, category: "HAIRCUT", active: true },
  { _id: "service7", name: "Bridal Hair & Makeup Package", description: "Complete bridal package with hair styling, makeup, saree draping, and trial session.", price: 50000, durationMinutes: 240, category: "BRIDAL", active: true },
  { _id: "service8", name: "Highlights/Balayage", description: "Partial highlights or balayage with natural-looking dimension and color blending.", price: 12000, durationMinutes: 150, category: "COLORING", active: true },
  { _id: "service9", name: "Blowdry & Styling", description: "Professional blow-dry with styling for special occasions or everyday elegance.", price: 2000, durationMinutes: 45, category: "STYLING", active: true },
  { _id: "service10", name: "Hair Extensions", description: "Premium hair extension installation with natural hair blending and styling.", price: 15000, durationMinutes: 120, category: "STYLING", active: true },
  
  // Salon 3 services (service11-15)
  { _id: "service11", name: "Classic Manicure", description: "Traditional manicure with nail shaping, cuticle care, massage, and polish.", price: 1500, durationMinutes: 45, category: "NAILS", active: true },
  { _id: "service12", name: "Spa Pedicure", description: "Luxurious pedicure with foot soak, scrub, massage, and polish.", price: 2500, durationMinutes: 60, category: "NAILS", active: true },
  { _id: "service13", name: "Gel Nails", description: "Long-lasting gel manicure with UV curing. Lasts 2-3 weeks without chipping.", price: 3500, durationMinutes: 90, category: "NAILS", active: true },
  { _id: "service14", name: "Facial Treatment (Basic)", description: "Deep cleansing facial with steam, extraction, mask, and moisturizing.", price: 3000, durationMinutes: 60, category: "SPA", active: true },
  { _id: "service15", name: "Full Body Massage", description: "Relaxing 90-minute full body massage with aromatherapy oils.", price: 6000, durationMinutes: 90, category: "SPA", active: true },
  
  // Salon 4 services (service16-20)
  { _id: "service16", name: "Bridal Trial Session", description: "Complete bridal makeup and hair trial before your wedding day.", price: 8000, durationMinutes: 120, category: "BRIDAL", active: true },
  { _id: "service17", name: "Party Makeup", description: "Glamorous makeup for parties, events, and special occasions.", price: 5000, durationMinutes: 60, category: "BRIDAL", active: true },
  { _id: "service18", name: "Saree Draping", description: "Professional saree draping in traditional or modern styles.", price: 2000, durationMinutes: 30, category: "BRIDAL", active: true },
  { _id: "service19", name: "Henna/Mehendi Design", description: "Beautiful henna designs for hands and feet. Traditional or contemporary patterns.", price: 3000, durationMinutes: 90, category: "BRIDAL", active: true },
  { _id: "service20", name: "Groom Grooming Package", description: "Complete grooming for grooms: haircut, shave, facial, and styling.", price: 5000, durationMinutes: 90, category: "BRIDAL", active: true },
  
  // Salon 5 services (service21-25)
  { _id: "service21", name: "Beard Trim & Shape", description: "Precision beard trimming and shaping with hot towel treatment.", price: 800, durationMinutes: 30, category: "HAIRCUT", active: true },
  { _id: "service22", name: "Hair Straightening", description: "Permanent hair straightening treatment for smooth, straight hair.", price: 18000, durationMinutes: 180, category: "TREATMENT", active: true },
  { _id: "service23", name: "Scalp Treatment", description: "Specialized scalp treatment for dandruff, dryness, or hair fall concerns.", price: 3500, durationMinutes: 60, category: "TREATMENT", active: true },
  { _id: "service24", name: "Hair Braiding Styles", description: "Creative braiding styles including box braids, cornrows, and fishtail.", price: 2500, durationMinutes: 90, category: "STYLING", active: true },
  { _id: "service25", name: "Color Touch-Up (Roots)", description: "Root color touch-up for maintaining your hair color between full treatments.", price: 3500, durationMinutes: 60, category: "COLORING", active: true },
  
  // Salon 6 services (service26-30)
  { _id: "service26", name: "Express Haircut", description: "Quick haircut service for busy professionals. No wash included.", price: 1200, durationMinutes: 20, category: "HAIRCUT", active: true },
  { _id: "service27", name: "Premium Facial Treatment", description: "Luxury anti-aging facial with serums, LED therapy, and face massage.", price: 8000, durationMinutes: 90, category: "SPA", active: true },
  { _id: "service28", name: "Waxing (Full Arms & Legs)", description: "Complete waxing service for smooth, hair-free arms and legs.", price: 2500, durationMinutes: 60, category: "SPA", active: true },
  { _id: "service29", name: "Threading (Eyebrows & Upper Lip)", description: "Precise threading for perfectly shaped eyebrows and upper lip hair removal.", price: 500, durationMinutes: 15, category: "THREADING", active: true },
  { _id: "service30", name: "Beach Waves Styling", description: "Relaxed, beachy waves perfect for casual and coastal vibes.", price: 2200, durationMinutes: 45, category: "STYLING", active: true }
]);

print("✅ Inserted " + db.services.countDocuments() + " services\n");

// ===================================================================
// 5. GENERATE TIME SLOTS (672 records - 7 days × 6 salons × 16 slots/day)
// ===================================================================

print("📝 Step 5: Generating Time Slots (this may take a moment)...");

// Helper function to format time
function formatTime(hour, minute) {
  return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
}

// Generate time slots
const salonIds = ["salon1", "salon2", "salon3", "salon4", "salon5", "salon6"];
const startDate = new Date("2025-10-11");
const days = 7;
const timeSlots = [];
let slotCounter = 1;

for (let dayOffset = 0; dayOffset < days; dayOffset++) {
  const currentDate = new Date(startDate);
  currentDate.setDate(currentDate.getDate() + dayOffset);
  const dateStr = currentDate.toISOString().split('T')[0];
  
  for (let salonId of salonIds) {
    // Generate slots from 9:00 AM to 6:00 PM (skip 1:00-2:00 PM lunch)
    const hours = [9, 10, 11, 12, 14, 15, 16, 17];
    
    for (let hour of hours) {
      for (let minute of [0, 30]) {
        const startTime = formatTime(hour, minute);
        const endMinute = minute + 30;
        const endHour = endMinute >= 60 ? hour + 1 : hour;
        const endTime = formatTime(endHour, endMinute % 60);
        
        // 70% available, 30% booked (random)
        const isAvailable = Math.random() < 0.7;
        
        timeSlots.push({
          _id: "slot" + String(slotCounter).padStart(3, '0'),
          date: dateStr,
          startTime: startTime,
          endTime: endTime,
          isAvailable: isAvailable,
          salon: { $ref: "salons", $id: salonId }
        });
        
        slotCounter++;
      }
    }
  }
}

// Insert time slots in batches
const batchSize = 100;
for (let i = 0; i < timeSlots.length; i += batchSize) {
  const batch = timeSlots.slice(i, i + batchSize);
  db.time_slots.insertMany(batch);
}

print("✅ Inserted " + db.time_slots.countDocuments() + " time slots\n");

// ===================================================================
// 6. NOTE: APPOINTMENTS & REVIEWS
// ===================================================================

print("📝 Step 6 & 7: Appointments and Reviews...");
print("⚠️  NOTE: Due to DBRef complexity, appointments and reviews should be inserted via:");
print("    1. MongoDB Compass (import JSON files)");
print("    2. Spring Boot DataInitializer.java");
print("    ");
print("    JSON files available:");
print("    - 6-appointments.json (25 appointments)");
print("    - 7-reviews.json (48 reviews)");
print("");

// ===================================================================
// VERIFICATION
// ===================================================================

print("✅ Database population complete!\n");
print("📊 Document Count Summary:");
print("   - Users:      " + db.users.countDocuments());
print("   - Salons:     " + db.salons.countDocuments());
print("   - Services:   " + db.services.countDocuments());
print("   - Customers:  " + db.customers.countDocuments());
print("   - Time Slots: " + db.time_slots.countDocuments());
print("   - Appointments: (insert via Compass or DataInitializer)");
print("   - Reviews:     (insert via Compass or DataInitializer)");
print("");
print("🔍 Verification Queries:");
print("   db.users.find().count()");
print("   db.salons.find().count()");
print("   db.time_slots.find({ isAvailable: true }).count()");
print("");
print("🚀 Next Steps:");
print("   1. Import appointments.json and reviews.json via MongoDB Compass");
print("   2. Or use Spring Boot DataInitializer.java for automatic insertion");
print("   3. Start your application and test!");
