package com.example.studentattandance.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentattandance.activities.WelcomeActivity;

/**
 * Utility class to help with session clearing across all activities
 * Ensures consistent session clearing behavior throughout the app
 */
public class SessionClearingHelper {
    
    private static final String TAG = "SessionClearingHelper";
    
    /**
     * Clear session and navigate to welcome screen
     * @param context The context to use for navigation
     * @param clearSession Whether to clear the session
     */
    public static void clearSessionAndNavigateToWelcome(Context context, boolean clearSession) {
        try {
            if (clearSession) {
                SessionManager sessionManager = SessionManager.getInstance(context);
                if (sessionManager != null) {
                    boolean cleared = sessionManager.performCompleteLogout();
                    Log.d(TAG, "Session clearing result: " + cleared);
                }
            }
            
            // Navigate to welcome screen
            Intent intent = new Intent(context, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error clearing session and navigating", e);
            // Emergency navigation
            try {
                Intent intent = new Intent(context, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            } catch (Exception emergencyError) {
                Log.e(TAG, "Emergency navigation also failed", emergencyError);
            }
        }
    }
    
    /**
     * Clear session and finish the current activity
     * @param activity The activity to finish
     */
    public static void clearSessionAndFinish(Activity activity) {
        try {
            if (activity != null) {
                // Clear session
                SessionManager sessionManager = SessionManager.getInstance(activity);
                if (sessionManager != null) {
                    sessionManager.performCompleteLogout();
                }
                
                // Finish the activity
                activity.finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing session and finishing activity", e);
            if (activity != null) {
                activity.finish();
            }
        }
    }
    
    /**
     * Check if session is valid and redirect if not
     * @param context The context to use
     * @return true if session is valid, false if redirected
     */
    public static boolean validateSessionAndRedirectIfNeeded(Context context) {
        try {
            SessionManager sessionManager = SessionManager.getInstance(context);
            if (sessionManager != null && sessionManager.isSessionValid()) {
                return true;
            } else {
                Log.w(TAG, "Session invalid, redirecting to welcome");
                clearSessionAndNavigateToWelcome(context, true);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error validating session", e);
            clearSessionAndNavigateToWelcome(context, true);
            return false;
        }
    }
    
    /**
     * Force clear session without navigation
     * @param context The context to use
     */
    public static void forceClearSession(Context context) {
        try {
            SessionManager sessionManager = SessionManager.getInstance(context);
            if (sessionManager != null) {
                sessionManager.forceClearSession();
                Log.d(TAG, "Session force cleared");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error force clearing session", e);
        }
    }
}
