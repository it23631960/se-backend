// MongoDB script to add 'type' field to existing salons
// Run this with: mongosh salon_booking update-salon-types.js

// Update existing salons with types based on their names/descriptions
db.salons.updateOne(
  { _id: "salon1" },
  { $set: { type: "hair-salon" } }
);

db.salons.updateOne(
  { _id: "salon2" },
  { $set: { type: "hair-salon" } }
);

db.salons.updateOne(
  { _id: "salon3" },
  { $set: { type: "barber-shop" } }
);

db.salons.updateOne(
  { _id: "salon4" },
  { $set: { type: "bridal-salon" } }
);

db.salons.updateOne(
  { _id: "salon5" },
  { $set: { type: "barber-shop" } }
);

db.salons.updateOne(
  { _id: "salon6" },
  { $set: { type: "nail-salon" } }
);

db.salons.updateOne(
  { _id: "salon7" },
  { $set: { type: "nail-salon" } }
);

db.salons.updateOne(
  { _id: "salon8" },
  { $set: { type: "bridal-salon" } }
);

// Verify the updates
print("Salons updated successfully!");
print("\nCurrent salon types:");
db.salons.find({}, { _id: 1, name: 1, type: 1 }).forEach(printjson);

print("\nCount by type:");
print("Hair Salons: " + db.salons.countDocuments({ type: "hair-salon" }));
print("Barber Shops: " + db.salons.countDocuments({ type: "barber-shop" }));
print("Nail Salons: " + db.salons.countDocuments({ type: "nail-salon" }));
print("Bridal Salons: " + db.salons.countDocuments({ type: "bridal-salon" }));
