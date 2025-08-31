package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.api.ApiService;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {
    
    private List<Object> reportsList;
    private boolean isStudent;
    private OnReportClickListener listener;
    
    public interface OnReportClickListener {
        void onReportClick(Object report);
    }
    
    public ReportsAdapter(List<Object> reportsList, boolean isStudent) {
        this.reportsList = reportsList;
        this.isStudent = isStudent;
    }
    
    public void setOnReportClickListener(OnReportClickListener listener) {
        this.listener = listener;
    }
    
    public void updateReports(List<Object> newReports) {
        this.reportsList = newReports;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object report = reportsList.get(position);
        holder.bind(report);
    }
    
    @Override
    public int getItemCount() {
        return reportsList != null ? reportsList.size() : 0;
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReportTitle;
        private TextView tvReportType;
        private TextView tvReportDate;
        private TextView tvReportSummary;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportTitle = itemView.findViewById(R.id.tv_report_title);
            tvReportType = itemView.findViewById(R.id.tv_report_type);
            tvReportDate = itemView.findViewById(R.id.tv_report_date);
            tvReportSummary = itemView.findViewById(R.id.tv_report_summary);
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onReportClick(reportsList.get(position));
                }
            });
        }
        
        public void bind(Object report) {
            if (report instanceof ApiService.StudentReport) {
                ApiService.StudentReport studentReport = (ApiService.StudentReport) report;
                tvReportTitle.setText("Student Attendance Report");
                tvReportType.setText("Personal Report");
                tvReportDate.setText("Current Semester");
                tvReportSummary.setText(String.format("Overall Attendance: %.1f%%", studentReport.overallAttendance));
            } else if (report instanceof ApiService.ClassReport) {
                ApiService.ClassReport classReport = (ApiService.ClassReport) report;
                tvReportTitle.setText(classReport.className);
                tvReportType.setText("Class Report");
                tvReportDate.setText(classReport.semester + " " + classReport.academicYear);
                tvReportSummary.setText(String.format("Average Attendance: %.1f%%", classReport.averageAttendance));
            } else {
                // Generic report display
                tvReportTitle.setText("Attendance Report");
                tvReportType.setText("General Report");
                tvReportDate.setText("N/A");
                tvReportSummary.setText("Report details available");
            }
        }
    }
}
