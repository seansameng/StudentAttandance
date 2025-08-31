package com.example.studentattandance.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.studentattandance.activities.LoginActivity;
import com.example.studentattandance.MainActivity;

import com.example.studentattandance.activities.RegisterActivity;
import com.example.studentattandance.activities.WelcomeActivity;
import com.example.studentattandance.database.entities.ClassEntity;
import com.example.studentattandance.database.entities.UserEntity;

public class NavigationHelper {
    
    private static NavigationHelper instance;
    private Context context;
    
    private NavigationHelper(Context context) {
        this.context = context;
    }
    
    public static NavigationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NavigationHelper(context);
        }
        return instance;
    }
    
    // Welcome Screen Navigation
    public void goToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
    
    public void goToRegister() {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
    
    // Authentication Navigation
    public void goToMainAfterLogin() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
    
    public void goToWelcomeAfterLogout() {
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
    

    

    
    // Fragment Navigation
    public void goToDashboard() {
        // This would typically navigate back to main activity with dashboard fragment
        Toast.makeText(context, "Dashboard", Toast.LENGTH_SHORT).show();
    }
    
    public void goToClasses() {
        // This would typically navigate back to main activity with classes fragment
        Toast.makeText(context, "Classes", Toast.LENGTH_SHORT).show();
    }
    
    public void goToAttendance() {
        // This would typically navigate back to main activity with attendance fragment
        Toast.makeText(context, "Attendance", Toast.LENGTH_SHORT).show();
    }
    
    // Back Navigation
    public void goBack(Context currentContext) {
        if (currentContext instanceof com.example.studentattandance.MainActivity) {
            // Exit app from main screen
            ((com.example.studentattandance.MainActivity) currentContext).finishAffinity();
        } else {
            // Go back to previous activity
            ((android.app.Activity) currentContext).finish();
        }
    }
    
    // Utility Methods
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    public void showError(String error) {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_LONG).show();
    }
}
