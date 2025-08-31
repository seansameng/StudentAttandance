package com.example.studentattandance.utils;

import android.util.Patterns;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class ValidationUtils {
    
    public static boolean validateRequired(TextInputLayout textInputLayout, String errorMessage) {
        EditText editText = textInputLayout.getEditText();
        if (editText == null) return false;
        
        String value = editText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputLayout.setError(errorMessage);
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }
    
    public static boolean validateEmail(TextInputLayout textInputLayout) {
        EditText editText = textInputLayout.getEditText();
        if (editText == null) return false;
        
        String email = editText.getText().toString().trim();
        if (email.isEmpty()) {
            textInputLayout.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputLayout.setError("Please enter a valid email address");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }
    
    public static boolean validatePassword(TextInputLayout textInputLayout, int minLength) {
        EditText editText = textInputLayout.getEditText();
        if (editText == null) return false;
        
        String password = editText.getText().toString();
        if (password.isEmpty()) {
            textInputLayout.setError("Password is required");
            return false;
        } else if (password.length() < minLength) {
            textInputLayout.setError("Password must be at least " + minLength + " characters");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }
    
    public static boolean validatePasswordMatch(TextInputLayout passwordLayout, TextInputLayout confirmPasswordLayout) {
        EditText passwordEditText = passwordLayout.getEditText();
        EditText confirmPasswordEditText = confirmPasswordLayout.getEditText();
        
        if (passwordEditText == null || confirmPasswordEditText == null) return false;
        
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        
        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            return false;
        } else {
            confirmPasswordLayout.setError(null);
            return true;
        }
    }
    
    public static boolean validatePhoneNumber(TextInputLayout textInputLayout) {
        EditText editText = textInputLayout.getEditText();
        if (editText == null) return false;
        
        String phoneNumber = editText.getText().toString().trim();
        if (!phoneNumber.isEmpty() && !Patterns.PHONE.matcher(phoneNumber).matches()) {
            textInputLayout.setError("Please enter a valid phone number");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }
    
    public static void clearError(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
    }
}




























