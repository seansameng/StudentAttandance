package com.example.studentattandance.api;

import android.content.Context;

import com.example.studentattandance.utils.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    
    private Context context;
    
    public AuthInterceptor(Context context) {
        this.context = context;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Get the access token from SessionManager
        SessionManager sessionManager = SessionManager.getInstance(context);
        String accessToken = sessionManager.getAccessToken();
        
        // If we have a token, add it to the request header
        if (accessToken != null && !accessToken.isEmpty()) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
            return chain.proceed(newRequest);
        }
        
        // If no token, proceed with the original request
        return chain.proceed(originalRequest);
    }
}
