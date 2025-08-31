package com.example.studentattandance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.studentattandance.models.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "StudentAttendancePrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_DATA = "user_data";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    private static final String KEY_AUTH_TYPE = "auth_type"; // Added for local auth type
    
    // Settings keys
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_DARK_MODE_ENABLED = "dark_mode_enabled";
    private static final String KEY_AUTO_SYNC_ENABLED = "auto_sync_enabled";

    
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private static SessionManager instance;
    
    private SessionManager(Context context) {
        try {
            Log.d("SessionManager", "Initializing SessionManager with context: " + (context != null));
            this.context = context;
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
            Log.d("SessionManager", "SessionManager initialized successfully");
        } catch (Exception e) {
            Log.e("SessionManager", "Error initializing SessionManager", e);
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize SessionManager", e);
        }
    }
    
    public static synchronized SessionManager getInstance(Context context) {
        try {
            if (instance == null && context != null) {
                Log.d("SessionManager", "Creating new SessionManager instance");
                instance = new SessionManager(context);
                Log.d("SessionManager", "SessionManager instance created successfully");
            } else if (context == null) {
                Log.e("SessionManager", "Context is null, cannot create instance");
                return null;
            }
            return instance;
        } catch (Exception e) {
            Log.e("SessionManager", "Error creating SessionManager instance", e);
            e.printStackTrace();
            return null;
        }
    }
    
    public void createLoginSession(String accessToken, String refreshToken, User user, long expiresIn) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_DATA, new Gson().toJson(user));
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (expiresIn * 1000));
        editor.apply();
    }
    
    // New method for local database authentication
    public void createLocalLoginSession(User user) {
        Log.d("SessionManager", "createLocalLoginSession() called with user: " + (user != null ? user.getUsername() : "null") + 
              ", role: " + (user != null ? user.getRole() : "null") + 
              ", id: " + (user != null ? user.getId() : "null"));
        
        if (user == null) {
            Log.e("SessionManager", "createLocalLoginSession() - user is null, cannot create session");
            return;
        }
        
        String userJson = new Gson().toJson(user);
        Log.d("SessionManager", "createLocalLoginSession() - User JSON to store: " + userJson);
        
        editor.putString(KEY_USER_DATA, userJson);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_AUTH_TYPE, "local"); // Mark as local auth
        // For local auth, we don't need tokens or expiry
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_TOKEN_EXPIRY);
        
        Log.d("SessionManager", "createLocalLoginSession() - About to apply changes to SharedPreferences");
        editor.apply();
        
        // Verify what was stored
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        String storedUserJson = prefs.getString(KEY_USER_DATA, null);
        String authType = prefs.getString(KEY_AUTH_TYPE, null);
        
        Log.d("SessionManager", "createLocalLoginSession() - Verification after apply:");
        Log.d("SessionManager", "  - KEY_IS_LOGGED_IN: " + isLoggedIn);
        Log.d("SessionManager", "  - KEY_USER_DATA: " + storedUserJson);
        Log.d("SessionManager", "  - KEY_AUTH_TYPE: " + authType);
        
        Log.d("SessionManager", "createLocalLoginSession() - Session created successfully");
    }
    
    public boolean isLoggedIn() {
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        if (!isLoggedIn) {
            return false;
        }
        
        // For local auth, just check if logged in flag is true
        // For API auth, check token expiry
        String authType = prefs.getString(KEY_AUTH_TYPE, "api");
        if ("local".equals(authType)) {
            return true;
        } else {
            return !isTokenExpired();
        }
    }
    
    public boolean isTokenExpired() {
        long expiryTime = prefs.getLong(KEY_TOKEN_EXPIRY, 0);
        return System.currentTimeMillis() >= expiryTime;
    }
    
    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }
    
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }
    
    public User getUser() {
        String userJson = prefs.getString(KEY_USER_DATA, null);
        Log.d("SessionManager", "getUser() called - userJson: " + userJson);
        if (userJson != null) {
            try {
                User user = new Gson().fromJson(userJson, User.class);
                Log.d("SessionManager", "getUser() - User parsed successfully: " + (user != null ? user.getUsername() : "null") + 
                      ", role: " + (user != null ? user.getRole() : "null"));
                return user;
            } catch (Exception e) {
                Log.e("SessionManager", "getUser() - Error parsing user JSON", e);
                return null;
            }
        }
        Log.d("SessionManager", "getUser() - No user data found");
        return null;
    }
    
    public String getUserData() {
        return prefs.getString(KEY_USER_DATA, null);
    }
    
    public void updateUser(User user) {
        editor.putString(KEY_USER_DATA, new Gson().toJson(user));
        editor.apply();
    }
    
    public void updateTokens(String accessToken, String refreshToken, long expiresIn) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (expiresIn * 1000));
        editor.apply();
    }
    
    public void logout() {
        try {
            Log.d("SessionManager", "Logging out user - clearing all session data");
            
            // Clear all preferences
            editor.clear();
            editor.apply();
            
            // Force commit to ensure data is cleared immediately
            editor.commit();
            
            Log.d("SessionManager", "User session cleared successfully");
        } catch (Exception e) {
            Log.e("SessionManager", "Error during logout", e);
            // Try alternative clearing method
            try {
                prefs.edit().clear().apply();
                Log.d("SessionManager", "Session cleared using alternative method");
            } catch (Exception fallbackError) {
                Log.e("SessionManager", "Fallback session clearing also failed", fallbackError);
            }
        }
    }
    
    public void forceClearSession() {
        try {
            // Clear all preferences
            editor.clear();
            editor.apply();
            
            // Force commit to ensure data is cleared immediately
            editor.commit();
            
            Log.d("SessionManager", "User session force cleared");
        } catch (Exception e) {
            Log.e("SessionManager", "Error force clearing session", e);
            // Try alternative clearing method
            try {
                prefs.edit().clear().apply();
                Log.d("SessionManager", "Session cleared using alternative method");
            } catch (Exception fallbackError) {
                Log.e("SessionManager", "Fallback session clearing also failed", fallbackError);
            }
        }
    }
    
    /**
     * Comprehensive logout that ensures all session data is cleared
     * and returns true if successful
     */
    public boolean performCompleteLogout() {
        try {
            Log.d("SessionManager", "Performing complete logout");
            logout();
            return true;
        } catch (Exception e) {
            Log.e("SessionManager", "Error during complete logout", e);
            return false;
        }
    }
    
    /**
     * Check if current session is valid and not expired
     */
    public boolean isSessionValid() {
        try {
            if (!isLoggedIn()) {
                return false;
            }
            
            User user = getUser();
            if (user == null) {
                return false;
            }
            
            // For API auth, check token expiry
            String authType = prefs.getString(KEY_AUTH_TYPE, "api");
            if ("api".equals(authType)) {
                return !isTokenExpired();
            }
            
            // For local auth, just check if user exists and is active
            return user.isActive();
        } catch (Exception e) {
            Log.e("SessionManager", "Error checking session validity", e);
            return false;
        }
    }
    
    public boolean isStudent() {
        User user = getUser();
        boolean isStudent = user != null && user.isStudent();
        Log.d("SessionManager", "isStudent() called - user: " + (user != null ? user.getUsername() : "null") + 
              ", role: " + (user != null ? user.getRole() : "null") + ", result: " + isStudent);
        return isStudent;
    }
    
    public boolean isTeacher() {
        User user = getUser();
        boolean isTeacher = user != null && user.isTeacher();
        Log.d("SessionManager", "isTeacher() called - user: " + (user != null ? user.getUsername() : "null") + 
              ", role: " + (user != null ? user.getRole() : "null") + ", result: " + isTeacher);
        return isTeacher;
    }
    
    public boolean isAdmin() {
        User user = getUser();
        boolean isAdmin = user != null && user.isAdmin();
        Log.d("SessionManager", "isAdmin() called - user: " + (user != null ? user.getUsername() : "null") + 
              ", role: " + (user != null ? user.getRole() : "null") + ", result: " + isAdmin);
        return isAdmin;
    }
    
    public String getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }
    
    public String getUserRole() {
        try {
            User user = getUser();
            if (user == null || user.getRole() == null) {
                Log.w("SessionManager", "getUserRole() called - user or role is null");
                return null;
            }
            
            String role = user.getRole().name();
            Log.d("SessionManager", "getUserRole() called - user: " + user.getUsername() + 
                  ", role enum: " + user.getRole() + ", role string: " + role);
            return role;
        } catch (Exception e) {
            Log.e("SessionManager", "Error getting user role", e);
            return null;
        }
    }
    
    public String getUsername() {
        User user = getUser();
        return user != null ? user.getUsername() : null;
    }
    
    public String getEmail() {
        User user = getUser();
        return user != null ? user.getEmail() : null;
    }
    
    public String getFirstName() {
        User user = getUser();
        return user != null ? user.getFirstName() : null;
    }
    
    public String getLastName() {
        User user = getUser();
        return user != null ? user.getLastName() : null;
    }
    
    // Settings preferences
    public boolean areNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }
    
    public void setNotificationsEnabled(boolean enabled) {
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled);
        editor.apply();
    }
    
    public boolean isDarkModeEnabled() {
        return prefs.getBoolean(KEY_DARK_MODE_ENABLED, false);
    }
    
    public void setDarkModeEnabled(boolean enabled) {
        editor.putBoolean(KEY_DARK_MODE_ENABLED, enabled);
        editor.apply();
    }
    
    public boolean isAutoSyncEnabled() {
        return prefs.getBoolean(KEY_AUTO_SYNC_ENABLED, true);
    }
    
    public void setAutoSyncEnabled(boolean enabled) {
        editor.putBoolean(KEY_AUTO_SYNC_ENABLED, enabled);
        editor.apply();
    }
    

}
