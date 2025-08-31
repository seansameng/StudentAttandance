package com.example.studentattandance.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.studentattandance.database.converters.DateConverter;
import com.example.studentattandance.database.dao.AttendanceDao;
import com.example.studentattandance.database.dao.ClassDao;
import com.example.studentattandance.database.dao.ClassEnrollmentDao;
import com.example.studentattandance.database.dao.UserDao;
import com.example.studentattandance.database.entities.AttendanceEntity;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.entities.ClassEnrollmentEntity;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.database.migrations.DatabaseMigrations;

@Database(
    entities = {
        UserEntity.class,
        ClassEntity.class,
        AttendanceEntity.class,
        ClassEnrollmentEntity.class
    },
    version = 2,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String TAG = "AppDatabase";
    private static final String DATABASE_NAME = "student_attendance_db";
    private static volatile AppDatabase INSTANCE;
    
    // DAOs
    public abstract UserDao userDao();
    public abstract ClassDao classDao();
    public abstract AttendanceDao attendanceDao();
    public abstract ClassEnrollmentDao classEnrollmentDao();
    
    // Singleton pattern
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    try {
                        Log.d(TAG, "Creating new database instance: " + DATABASE_NAME);
                        
                        // Ensure we have a valid application context
                        Context appContext = context.getApplicationContext();
                        if (appContext == null) {
                            Log.e(TAG, "Application context is null!");
                            throw new RuntimeException("Application context is null");
                        }
                        
                        INSTANCE = Room.databaseBuilder(
                            appContext,
                            AppDatabase.class,
                            DATABASE_NAME
                        )
                        .addMigrations(DatabaseMigrations.MIGRATION_1_2)
                        .fallbackToDestructiveMigration()
                        .addCallback(new RoomDatabase.Callback() {
                            @Override
                            public void onCreate(androidx.sqlite.db.SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                Log.d(TAG, "Database created successfully: " + DATABASE_NAME);
                            }
                            
                            @Override
                            public void onOpen(androidx.sqlite.db.SupportSQLiteDatabase db) {
                                super.onOpen(db);
                                Log.d(TAG, "Database opened successfully: " + DATABASE_NAME);
                            }
                            
                            @Override
                            public void onDestructiveMigration(androidx.sqlite.db.SupportSQLiteDatabase db) {
                                super.onDestructiveMigration(db);
                                Log.w(TAG, "Destructive migration occurred - database recreated");
                            }
                        })
                        .build();
                        
                        Log.d(TAG, "Database instance created successfully: " + DATABASE_NAME);
                        
                    } catch (Exception e) {
                        Log.e(TAG, "Error creating database instance", e);
                        Log.e(TAG, "Error details: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("Failed to create database: " + e.getMessage(), e);
                    }
                }
            }
        }
        
        return INSTANCE;
    }
    
    // Close database
    public static void closeDatabase() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            try {
                INSTANCE.close();
                Log.d(TAG, "Database closed successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error closing database", e);
            } finally {
                INSTANCE = null;
            }
        }
    }
    
    // Reset database instance (for testing/debugging)
    public static void resetDatabase() {
        if (INSTANCE != null) {
            try {
                if (INSTANCE.isOpen()) {
                    INSTANCE.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error closing database during reset", e);
            } finally {
                INSTANCE = null;
                Log.d(TAG, "Database instance reset");
            }
        }
    }
}
