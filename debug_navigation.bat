@echo off
echo ========================================
echo Navigation Debug Script
echo ========================================
echo.

echo Building the app...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo Build failed! Please check the errors above.
    pause
    exit /b 1
)

echo.
echo Build successful! Now testing navigation...
echo.

echo Installing the app...
call gradlew installDebug
if %errorlevel% neq 0 (
    echo Install failed! Please check if a device is connected.
    pause
    exit /b 1
)

echo.
echo App installed successfully!
echo.
echo To test navigation:
echo 1. Open the app
echo 2. Login with any valid credentials
echo 3. Check the logcat for navigation logs
echo 4. Try clicking different menu items
echo.
echo To view logs, run: adb logcat | findstr "MainActivity"
echo.
pause
















