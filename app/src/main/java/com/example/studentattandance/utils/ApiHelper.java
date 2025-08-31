package com.example.studentattandance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.studentattandance.api.ApiClient;

public class ApiHelper {
    
    private static final String PREF_NAME = "ApiConfigPrefs";
    private static final String KEY_BASE_URL = "base_url";
    
    // Predefined URLs for easy switching (PHP backend)
    public static final String URL_EMULATOR = "http://10.0.2.2:8080/api/";
    public static final String URL_LOCALHOST = "http://localhost/api/";
    public static final String URL_PRODUCTION = "https://yourdomain.com/api/";
    
    public static void updateBaseUrl(Context context, String newBaseUrl) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_BASE_URL, newBaseUrl);
        editor.apply();
        
        // Update the API client
        ApiClient.getInstance(context).updateBaseUrl(newBaseUrl);
        
        Toast.makeText(context, "API URL updated to: " + newBaseUrl, Toast.LENGTH_LONG).show();
    }
    
    public static String getCurrentBaseUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_BASE_URL, URL_EMULATOR);
    }
    
    public static void switchToEmulator(Context context) {
        updateBaseUrl(context, URL_EMULATOR);
    }
    
    public static void switchToLocalhost(Context context) {
        updateBaseUrl(context, URL_LOCALHOST);
    }
    
    public static void switchToProduction(Context context) {
        updateBaseUrl(context, URL_PRODUCTION);
    }
    
    public static void switchToCustomUrl(Context context, String customUrl) {
        // Ensure the URL ends with /api/
        if (!customUrl.endsWith("/api/")) {
            if (customUrl.endsWith("/")) {
                customUrl += "api/";
            } else {
                customUrl += "/api/";
            }
        }
        updateBaseUrl(context, customUrl);
    }
}
