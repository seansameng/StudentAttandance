Write-Host "========================================" -ForegroundColor Green
Write-Host "Student Attendance App - Build Script" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "Cleaning previous build..." -ForegroundColor Yellow
& ./gradlew clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error during clean. Please check your setup." -ForegroundColor Red
    Read-Host "Press Enter to continue"
    exit 1
}

Write-Host ""
Write-Host "Building debug APK..." -ForegroundColor Yellow
& ./gradlew assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed. Please check the error messages above." -ForegroundColor Red
    Read-Host "Press Enter to continue"
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Build completed successfully!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Your APK is located at:" -ForegroundColor Cyan
Write-Host "app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor White
Write-Host ""
Write-Host "To install and run on a connected device:" -ForegroundColor Cyan
Write-Host "& ./gradlew installDebug" -ForegroundColor White
Write-Host ""
Write-Host "To run the app:" -ForegroundColor Cyan
Write-Host "& ./gradlew runDebug" -ForegroundColor White
Write-Host ""
Read-Host "Press Enter to continue"

















