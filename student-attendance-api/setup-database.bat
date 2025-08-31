@echo off
echo Setting up Student Attendance Database...
echo.

echo 1. Starting MySQL service...
net start MySQL80
if %errorlevel% neq 0 (
    echo MySQL service not found. Please check if MySQL is installed and running.
    echo You may need to start it manually or check the service name.
    pause
    exit /b 1
)

echo.
echo 2. Creating database...
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS attendance_system;"
if %errorlevel% neq 0 (
    echo Failed to create database. Please check MySQL connection.
    pause
    exit /b 1
)

echo.
echo 3. Database setup complete!
echo Database name: attendance_system
echo Port: 3308
echo Username: root
echo.
echo You can now start your Spring Boot application.
pause
