package com.example.studentattandance.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Response;

public class SpringBootResponseHandler {
    
    private static final Gson gson = new Gson();
    
    /**
     * Handle Spring Boot API responses with proper error handling
     */
    public static <T> boolean handleResponse(Context context, Response<T> response, String operation) {
        if (response.isSuccessful()) {
            return true;
        }
        
        // Handle different HTTP status codes
        switch (response.code()) {
            case 400:
                showError(context, "Bad Request: " + getErrorMessage(response), operation);
                break;
            case 401:
                showError(context, "Unauthorized: Please login again", operation);
                // TODO: Trigger token refresh or logout
                break;
            case 403:
                showError(context, "Access Denied: You don't have permission", operation);
                break;
            case 404:
                showError(context, "Not Found: " + getErrorMessage(response), operation);
                break;
            case 409:
                showError(context, "Conflict: " + getErrorMessage(response), operation);
                break;
            case 422:
                showError(context, "Validation Error: " + getErrorMessage(response), operation);
                break;
            case 500:
                showError(context, "Server Error: Please try again later", operation);
                break;
            case 503:
                showError(context, "Service Unavailable: Please try again later", operation);
                break;
            default:
                showError(context, "Error " + response.code() + ": " + getErrorMessage(response), operation);
                break;
        }
        
        return false;
    }
    
    /**
     * Extract error message from Spring Boot error response
     */
    private static String getErrorMessage(Response<?> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            if (!errorBody.isEmpty()) {
                JsonObject jsonObject = JsonParser.parseString(errorBody).getAsJsonObject();
                
                // Spring Boot standard error fields
                if (jsonObject.has("message")) {
                    return jsonObject.get("message").getAsString();
                }
                if (jsonObject.has("error")) {
                    return jsonObject.get("error").getAsString();
                }
                if (jsonObject.has("detail")) {
                    return jsonObject.get("detail").getAsString();
                }
                
                return errorBody;
            }
        } catch (Exception e) {
            // Fallback to response message
        }
        
        return response.message() != null ? response.message() : "Unknown error";
    }
    
    /**
     * Show error message to user
     */
    private static void showError(Context context, String message, String operation) {
        String fullMessage = operation + " failed: " + message;
        Toast.makeText(context, fullMessage, Toast.LENGTH_LONG).show();
    }
    
    /**
     * Check if response indicates token expiration
     */
    public static boolean isTokenExpired(Response<?> response) {
        return response.code() == 401;
    }
    
    /**
     * Check if response indicates server error
     */
    public static boolean isServerError(Response<?> response) {
        return response.code() >= 500;
    }
    
    /**
     * Check if response indicates client error
     */
    public static boolean isClientError(Response<?> response) {
        return response.code() >= 400 && response.code() < 500;
    }
}
