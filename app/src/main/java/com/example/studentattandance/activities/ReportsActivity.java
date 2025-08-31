package com.example.studentattandance.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.adapters.ReportsAdapter;
import com.example.studentattandance.api.ApiClient;
import com.example.studentattandance.api.ApiService;
import com.example.studentattandance.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsActivity extends AppCompatActivity {
    
    private TextView tvTitle;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Spinner spinnerClass;
    private Button btnGenerateReport;
    private Button btnExportReport;
    private RecyclerView rvReports;
    
    private ApiService apiService;
    private SessionManager sessionManager;
    private ReportsAdapter reportsAdapter;
    private String startDate;
    private String endDate;
    private String selectedClassId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupDatePickers();
        
        apiService = ApiClient.getInstance(this).getApiService();
        sessionManager = SessionManager.getInstance(this);
        
        // Set title based on user role
        if (sessionManager.isStudent()) {
            tvTitle.setText(R.string.my_reports);
            spinnerClass.setVisibility(View.GONE);
        } else {
            tvTitle.setText(R.string.attendance_reports);
        }
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        startDatePicker = findViewById(R.id.start_date_picker);
        endDatePicker = findViewById(R.id.end_date_picker);
        spinnerClass = findViewById(R.id.spinner_class);
        btnGenerateReport = findViewById(R.id.btn_generate_report);
        btnExportReport = findViewById(R.id.btn_export_report);
        rvReports = findViewById(R.id.rv_reports);
    }
    
    private void setupRecyclerView() {
        reportsAdapter = new ReportsAdapter(new ArrayList<>(), sessionManager.isStudent());
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        rvReports.setAdapter(reportsAdapter);
    }
    
    private void setupClickListeners() {
        btnGenerateReport.setOnClickListener(v -> generateReport());
        btnExportReport.setOnClickListener(v -> exportReport());
    }
    
    private void setupDatePickers() {
        // Set default dates (current month)
        Calendar calendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.DAY_OF_MONTH, 1); // First day of month
        
        startDatePicker.init(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), 
                           startCalendar.get(Calendar.DAY_OF_MONTH), (view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            startDate = sdf.format(selectedCalendar.getTime());
        });
        
        endDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
                         calendar.get(Calendar.DAY_OF_MONTH), (view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            endDate = sdf.format(selectedCalendar.getTime());
        });
        
        // Set initial dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startDate = sdf.format(startCalendar.getTime());
        endDate = sdf.format(calendar.getTime());
    }
    
    private void generateReport() {
        if (startDate == null || endDate == null) {
            showError("Please select start and end dates");
            return;
        }
        
        // Show progress
        btnGenerateReport.setEnabled(false);
        btnGenerateReport.setText("Generating...");
        
        if (sessionManager.isStudent()) {
            generateStudentReport();
        } else {
            generateClassReport();
        }
    }
    
    private void generateStudentReport() {
        // Generate report for current student
        apiService.getStudentReport("Bearer " + sessionManager.getAccessToken(), sessionManager.getUserId())
                .enqueue(new Callback<ApiService.StudentReport>() {
                    @Override
                    public void onResponse(Call<ApiService.StudentReport> call, 
                                        Response<ApiService.StudentReport> response) {
                        btnGenerateReport.setEnabled(true);
                        btnGenerateReport.setText("Generate Report");
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiService.StudentReport studentReport = response.body();
                            displayStudentReport(studentReport);
                        } else {
                            showError("Failed to generate student report");
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiService.StudentReport> call, Throwable t) {
                        btnGenerateReport.setEnabled(true);
                        btnGenerateReport.setText("Generate Report");
                        showError("Network error. Please try again.");
                    }
                });
    }
    
    private void generateClassReport() {
        if (selectedClassId == null) {
            showError("Please select a class");
            btnGenerateReport.setEnabled(true);
            btnGenerateReport.setText("Generate Report");
            return;
        }
        
        // Generate report for selected class
        apiService.getClassReport("Bearer " + sessionManager.getAccessToken(), selectedClassId)
                .enqueue(new Callback<ApiService.ClassReport>() {
                    @Override
                    public void onResponse(Call<ApiService.ClassReport> call, 
                                        Response<ApiService.ClassReport> response) {
                        btnGenerateReport.setEnabled(true);
                        btnGenerateReport.setText("Generate Report");
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiService.ClassReport classReport = response.body();
                            displayClassReport(classReport);
                        } else {
                            showError("Failed to generate class report");
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiService.ClassReport> call, Throwable t) {
                        btnGenerateReport.setEnabled(true);
                        btnGenerateReport.setText("Generate Report");
                        showError("Network error. Please try again.");
                    }
                });
    }
    
    private void displayStudentReport(ApiService.StudentReport studentReport) {
        List<Object> reportsList = new ArrayList<>();
        reportsList.add(studentReport);
        reportsAdapter.updateReports(reportsList);
        
        btnExportReport.setEnabled(true);
        Toast.makeText(this, "Student report generated successfully", Toast.LENGTH_SHORT).show();
    }
    
    private void displayClassReport(ApiService.ClassReport classReport) {
        List<Object> reportsList = new ArrayList<>();
        reportsList.add(classReport);
        reportsAdapter.updateReports(reportsList);
        
        btnExportReport.setEnabled(true);
        Toast.makeText(this, "Class report generated successfully", Toast.LENGTH_SHORT).show();
    }
    
    private void exportReport() {
        // TODO: Implement report export functionality
        Toast.makeText(this, "Export feature coming soon", Toast.LENGTH_SHORT).show();
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
