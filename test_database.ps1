Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Database Connection Test Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "This script will help you test the database connection" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Make sure your Android device/emulator is connected" -ForegroundColor White
Write-Host "2. Make sure the app is installed" -ForegroundColor White
Write-Host "3. Run the following commands:" -ForegroundColor White
Write-Host ""
Write-Host "adb shell am start -n com.example.studentattandance/.utils.DatabaseTestActivity" -ForegroundColor Green
Write-Host ""
Write-Host "This will open a test activity that shows database status" -ForegroundColor Yellow
Write-Host ""
Write-Host "To view logs:" -ForegroundColor White
Write-Host "adb logcat | Select-String -Pattern '(DatabaseTester|AppDatabase|DataRepository)'" -ForegroundColor Green
Write-Host ""
Write-Host "Press any key to continue..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
