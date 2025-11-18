// MongoDB script to delete and re-import salons with type field
// Run this with: mongosh salon_booking delete-and-reimport-salons.js

print("Step 1: Deleting existing salons...");
const deleteResult = db.salons.deleteMany({});
print("Deleted " + deleteResult.deletedCount + " salon(s)");

print("\nStep 2: Please run this command to import new data:");
print("mongoimport --db salon_booking --collection salons --file sample-data/2-salons.json --jsonArray");

print("\nOr use this PowerShell command:");
print("mongoimport --db salon_booking --collection salons --file 'c:\\Users\\hasit\\Desktop\\SE Project\\backend\\sample-data\\2-salons.json' --jsonArray");
