@echo off
echo ========================================
echo Database Connection Test Script
echo ========================================
echo.
echo This script will help you test the database connection
echo.
echo 1. Make sure your Android device/emulator is connected
echo 2. Make sure the app is installed
echo 3. Run the following commands:
echo.
echo adb shell am start -n com.example.studentattandance/.utils.DatabaseTestActivity
echo.
echo This will open a test activity that shows database status
echo.
echo To view logs:
echo adb logcat | grep -E "(DatabaseTester|AppDatabase|DataRepository)"
echo.
echo Press any key to continue...
pause > nul
