@echo off
echo ========================================
echo Student Attendance App - Debug Script
echo ========================================
echo.

echo Checking if app is installed...
adb shell pm list packages | findstr studentattandance
if %errorlevel% neq 0 (
    echo App is not installed. Installing now...
    call gradlew installDebug
) else (
    echo App is already installed.
)

echo.
echo Checking device status...
adb devices

echo.
echo Attempting to launch app...
adb shell am start -n com.example.studentattandance/.activities.WelcomeActivity

echo.
echo Checking app logs for errors...
echo Press Ctrl+C to stop logcat
adb logcat | findstr "StudentAttendance\|AndroidRuntime\|FATAL"

















