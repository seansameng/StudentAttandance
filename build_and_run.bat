@echo off
echo ========================================
echo Student Attendance App - Build Script
echo ========================================
echo.

echo Cleaning previous build...
call gradlew clean
if %errorlevel% neq 0 (
    echo Error during clean. Please check your setup.
    pause
    exit /b 1
)

echo.
echo Building debug APK...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo Build failed. Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build completed successfully!
echo ========================================
echo.
echo Your APK is located at:
echo app\build\outputs\apk\debug\app-debug.apk
echo.
echo To install and run on a connected device:
echo call gradlew installDebug
echo.
echo To run the app:
echo call gradlew runDebug
echo.
pause

















