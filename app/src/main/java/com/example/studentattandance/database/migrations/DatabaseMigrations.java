package com.example.studentattandance.database.migrations;

import android.util.Log;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigrations {
    
    private static final String TAG = "DatabaseMigrations";
    
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Log.d(TAG, "Starting migration from version 1 to 2");
            
            try {
                // Step 1: Disable foreign key constraints temporarily
                database.execSQL("PRAGMA foreign_keys=OFF");
                Log.d(TAG, "Foreign key constraints disabled");
                
                // Step 2: Create the new class_enrollments table
                Log.d(TAG, "Creating class_enrollments table...");
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `class_enrollments` (" +
                    "`id` TEXT NOT NULL, " +
                    "`studentId` TEXT NOT NULL, " +
                    "`classId` TEXT NOT NULL, " +
                    "`enrolledAt` INTEGER, " +
                    "`isActive` INTEGER NOT NULL DEFAULT 1, " +
                    "PRIMARY KEY(`id`))"
                );
                
                // Step 3: Create indices for better performance
                Log.d(TAG, "Creating indices for class_enrollments...");
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_class_enrollments_studentId_classId` ON `class_enrollments` (`studentId`, `classId`)"
                );
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_class_enrollments_studentId` ON `class_enrollments` (`studentId`)"
                );
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_class_enrollments_classId` ON `class_enrollments` (`classId`)"
                );
                
                // Step 4: Add indices to existing tables for better performance
                Log.d(TAG, "Adding indices to existing tables...");
                addIndicesToExistingTables(database);
                
                // Step 5: Re-enable foreign key constraints
                database.execSQL("PRAGMA foreign_keys=ON");
                Log.d(TAG, "Foreign key constraints re-enabled");
                
                // Step 6: Add foreign key constraints after table creation
                Log.d(TAG, "Adding foreign key constraints...");
                try {
                    database.execSQL(
                        "ALTER TABLE `class_enrollments` ADD CONSTRAINT `fk_class_enrollments_student` " +
                        "FOREIGN KEY(`studentId`) REFERENCES `users`(`id`) ON DELETE CASCADE"
                    );
                    database.execSQL(
                        "ALTER TABLE `class_enrollments` ADD CONSTRAINT `fk_class_enrollments_class` " +
                        "FOREIGN KEY(`classId`) REFERENCES `classes`(`id`) ON DELETE CASCADE"
                    );
                    Log.d(TAG, "Foreign key constraints added successfully");
                } catch (Exception e) {
                    Log.w(TAG, "Could not add foreign key constraints: " + e.getMessage());
                    Log.w(TAG, "This is normal for new installations");
                }
                
                Log.d(TAG, "Migration from version 1 to 2 completed successfully");
                
            } catch (Exception e) {
                Log.e(TAG, "Migration failed: " + e.getMessage(), e);
                
                // Try to clean up and re-enable foreign keys
                try {
                    database.execSQL("PRAGMA foreign_keys=ON");
                } catch (Exception cleanupError) {
                    Log.e(TAG, "Failed to re-enable foreign keys during cleanup", cleanupError);
                }
                
                throw new RuntimeException("Migration failed: " + e.getMessage(), e);
            }
        }
        
        private void addIndicesToExistingTables(SupportSQLiteDatabase database) {
            try {
                // Check if attendance table exists before creating indices
                if (tableExists(database, "attendance")) {
                    Log.d(TAG, "Adding indices to attendance table...");
                    database.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_attendance_studentId_classId_date` ON `attendance` (`studentId`, `classId`, `date`)"
                    );
                    database.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_attendance_studentId` ON `attendance` (`studentId`)"
                    );
                    database.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_attendance_classId` ON `attendance` (`classId`)"
                    );
                    database.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_attendance_date` ON `attendance` (`date`)"
                    );
                }
                
                // Check if classes table exists before creating indices
                if (tableExists(database, "classes")) {
                    Log.d(TAG, "Adding indices to classes table...");
                    database.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_classes_teacherId` ON `classes` (`teacherId`)"
                    );
                    database.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_classes_subject` ON `classes` (`subject`)"
                    );
                }
                
                // Check if users table exists before creating indices
                if (tableExists(database, "users")) {
                    Log.d(TAG, "Adding indices to users table...");
                    database.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_users_username` ON `users` (`username`)"
                    );
                    database.execSQL(
                        "CREATE INDEX IF NOT EXISTS `index_users_role` ON `users` (`role`)"
                    );
                }
                
            } catch (Exception e) {
                Log.w(TAG, "Could not add indices to existing tables: " + e.getMessage());
                // This is not critical, continue with migration
            }
        }
        
        // Helper method to check if a table exists
        private boolean tableExists(SupportSQLiteDatabase database, String tableName) {
            try {
                android.database.Cursor cursor = database.query(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name=?", 
                    new String[]{tableName}
                );
                boolean exists = cursor != null && cursor.getCount() > 0;
                if (cursor != null) {
                    cursor.close();
                }
                return exists;
            } catch (Exception e) {
                Log.w(TAG, "Error checking if table " + tableName + " exists: " + e.getMessage());
                return false;
            }
        }
    };
}
