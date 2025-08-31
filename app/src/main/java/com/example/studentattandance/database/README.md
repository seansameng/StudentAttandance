# Room Database Documentation

## Overview
This project uses Room database for local data storage in the Student Attendance Android app. The database is designed to handle users, classes, attendance records, and class enrollments.

## Database Structure

### Version: 2
- **Users Table**: Stores user information (students, teachers, admins)
- **Classes Table**: Stores class information with teacher assignments
- **Attendance Table**: Stores attendance records for students in classes
- **Class Enrollments Table**: Manages student-class relationships

## Entities

### UserEntity
- **Table**: `users`
- **Primary Key**: `id` (String)
- **Fields**: username, email, firstName, lastName, role, createdAt, lastLogin
- **Indices**: username (unique), role

### ClassEntity
- **Table**: `classes`
- **Primary Key**: `id` (String)
- **Fields**: className, subject, teacherId, schedule, room, createdAt
- **Foreign Keys**: teacherId → users.id
- **Indices**: teacherId, subject

### AttendanceEntity
- **Table**: `attendance`
- **Primary Key**: `id` (String)
- **Fields**: studentId, classId, date, status, createdAt
- **Foreign Keys**: 
  - studentId → users.id
  - classId → classes.id
- **Indices**: (studentId, classId, date) unique, studentId, classId, date

### ClassEnrollmentEntity
- **Table**: `class_enrollments`
- **Primary Key**: `id` (String)
- **Fields**: studentId, classId, enrolledAt, isActive
- **Foreign Keys**:
  - studentId → users.id
  - classId → classes.id
- **Indices**: (studentId, classId) unique, studentId, classId

## DAOs (Data Access Objects)

### UserDao
- Basic CRUD operations
- Search by username, email
- Filter by role (STUDENT, TEACHER, ADMIN)
- Count users by role

### ClassDao
- Basic CRUD operations
- Filter by teacher, subject, room, schedule
- Search classes by name or subject
- Count classes by teacher

### AttendanceDao
- Basic CRUD operations
- Filter by student, class, date
- Count attendance by status (PRESENT, ABSENT, LATE)
- Get attendance history and reports

### ClassEnrollmentDao
- Basic CRUD operations
- Filter by student, class
- Count active enrollments
- Update enrollment status

## Repository Pattern

The `DataRepository` class provides a clean API for database operations:
- Handles background execution using ExecutorService
- Provides LiveData for reactive UI updates
- Centralizes all database operations

## Database Configuration

### AppDatabase
- **Name**: `student_attendance_db`
- **Version**: 2
- **Migration**: Includes migration from version 1 to 2
- **Type Converters**: DateConverter for Date handling

### Migration Strategy
- **Version 1 → 2**: Adds ClassEnrollmentEntity table and indices
- **Fallback**: Uses destructive migration if needed

## Usage Examples

### Basic Database Operations
```java
// Get database instance
AppDatabase database = AppDatabase.getInstance(context);

// Get DAOs
UserDao userDao = database.userDao();
ClassDao classDao = database.classDao();
AttendanceDao attendanceDao = database.attendanceDao();
ClassEnrollmentDao enrollmentDao = database.classEnrollmentDao();
```

### Using Repository
```java
// Initialize repository
DataRepository repository = new DataRepository(context);

// Insert user
UserEntity user = new UserEntity();
user.setId(DatabaseHelper.generateId());
user.setUsername("john_doe");
user.setRole("STUDENT");
repository.insertUser(user);

// Observe users
repository.getAllUsers().observe(this, users -> {
    // Update UI with user list
});
```

### Database Helper Utilities
```java
DatabaseHelper helper = new DatabaseHelper(context);

// Check database health
boolean isHealthy = helper.isDatabaseHealthy();

// Get database statistics
helper.getDatabaseStats(new DatabaseHelper.DatabaseStatsCallback() {
    @Override
    public void onStatsReceived(int userCount, int classCount, 
                               int attendanceCount, int enrollmentCount) {
        // Handle statistics
    }
    
    @Override
    public void onError(Exception e) {
        // Handle error
    }
});
```

## Best Practices

1. **Always use Repository pattern** for database operations
2. **Use LiveData** for reactive UI updates
3. **Execute database operations on background threads**
4. **Handle database errors gracefully**
5. **Use indices** for frequently queried fields
6. **Implement proper migration strategies** for schema changes

## Performance Considerations

- **Indices**: Added on frequently queried fields
- **Foreign Keys**: Ensure data integrity
- **Background Execution**: All database operations run on background threads
- **LiveData**: Efficient UI updates with lifecycle awareness

## Testing

The database can be tested using:
- **Unit Tests**: Test DAOs and Repository methods
- **Instrumented Tests**: Test actual database operations
- **Database Viewer**: Use Android Studio's Database Inspector

## Troubleshooting

### Common Issues
1. **Migration Errors**: Check migration implementation
2. **Foreign Key Violations**: Ensure referenced data exists
3. **Performance Issues**: Check indices and query optimization
4. **Memory Leaks**: Properly close database connections

### Debug Tools
- **Database Inspector**: View database contents in Android Studio
- **Logcat**: Check for database-related errors
- **Room Schema**: Export schema for debugging
