package com.example.studentattandance.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // For Android Emulator (Spring Boot backend)

// ... existing code ...
private static final String BASE_URL = "http://10.0.2.2:8080/api/";
// ... existing code ...


    // For physical device (PHP backend)
    // private static final String BASE_URL = "http://192.168.1.100/api/";
    
    
    // For Android Emulator (Spring Boot backend)
   // private static final String BASE_URL = "http://10.0.2.2:8080/";
    
    // For physical device (Spring Boot backend) - replace with your actual IP
//    private static final String BASE_URL = "http://192.168.1.100:8080/";
    private static ApiClient instance;
    private Retrofit retrofit;
    private ApiService apiService;
    private Context context;

    private ApiClient(Context context) {
        this.context = context;
        
        // Create logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttp client with interceptors
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new AuthInterceptor(context))
                .build();

        // Create Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create API service
        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void updateBaseUrl(String newBaseUrl) {
        // Create new OkHttp client with interceptors
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new AuthInterceptor(context))
                .build();

        // Create new Retrofit instance with new base URL
        retrofit = new Retrofit.Builder()
                .baseUrl(newBaseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Update API service
        apiService = retrofit.create(ApiService.class);
    }
}
