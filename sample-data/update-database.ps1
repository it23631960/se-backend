# PowerShell script to update MongoDB salons with type field
# Run this from the sample-data directory

Write-Host "=== MongoDB Salon Type Update Script ===" -ForegroundColor Cyan
Write-Host ""

$dbName = "salon_booking"
$collectionName = "salons"

Write-Host "Choose an option:" -ForegroundColor Yellow
Write-Host "1. Update existing salons with type field (recommended if you have bookings/reviews)"
Write-Host "2. Delete all salons and re-import with type field (clean slate)"
Write-Host ""

$choice = Read-Host "Enter your choice (1 or 2)"

if ($choice -eq "1") {
    Write-Host "`nUpdating existing salons..." -ForegroundColor Green
    mongosh $dbName update-salon-types.js
}
elseif ($choice -eq "2") {
    Write-Host "`nDeleting existing salons..." -ForegroundColor Yellow
    mongosh $dbName delete-and-reimport.js
    
    Write-Host "`nImporting new salon data..." -ForegroundColor Green
    mongoimport --db $dbName --collection $collectionName --file "2-salons.json" --jsonArray
    
    Write-Host "`nVerifying import..." -ForegroundColor Cyan
    mongosh $dbName --eval "db.salons.find({}, {_id:1, name:1, type:1}).forEach(printjson)"
}
else {
    Write-Host "Invalid choice. Exiting." -ForegroundColor Red
    exit
}

Write-Host "`n=== Update Complete! ===" -ForegroundColor Green
Write-Host "You can now test your endpoints:" -ForegroundColor Cyan
Write-Host "  GET http://localhost:8080/api/hair-salons" -ForegroundColor White
Write-Host "  GET http://localhost:8080/api/barber-shops" -ForegroundColor White
Write-Host "  GET http://localhost:8080/api/nail-salons" -ForegroundColor White
Write-Host "  GET http://localhost:8080/api/bridal-salons" -ForegroundColor White
