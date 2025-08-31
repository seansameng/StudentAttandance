package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.AttendanceEntity;

import java.util.List;

public class AttendanceViewAdapter extends RecyclerView.Adapter<AttendanceViewAdapter.AttendanceViewHolder> {
    
    private List<AttendanceEntity> attendanceRecords;
    
    public AttendanceViewAdapter(List<AttendanceEntity> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }
    
    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_view, parent, false);
        return new AttendanceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceEntity record = attendanceRecords.get(position);
        holder.bind(record);
    }
    
    @Override
    public int getItemCount() {
        return attendanceRecords.size();
    }
    
    public void updateData(List<AttendanceEntity> newRecords) {
        this.attendanceRecords = newRecords;
        notifyDataSetChanged();
    }
    
    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName, tvDate, tvStatus, tvTime;
        
        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tv_class_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
        
        public void bind(AttendanceEntity record) {
            tvClassName.setText(record.getClassName());
            tvDate.setText(record.getDate());
            tvStatus.setText(record.getStatus());
            
            // Set status color
            switch (record.getStatus()) {
                case "PRESENT":
                    tvStatus.setTextColor(0xFF27AE60); // Green
                    break;
                case "ABSENT":
                    tvStatus.setTextColor(0xFFE74C3C); // Red
                    break;
                case "LATE":
                    tvStatus.setTextColor(0xFFF39C12); // Orange
                    break;
                default:
                    tvStatus.setTextColor(0xFF7F8C8D); // Gray
                    break;
            }
            
            // Format time if available
            if (record.getMarkedAt() != null) {
                java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                tvTime.setText("Marked at: " + timeFormat.format(record.getMarkedAt()));
            } else {
                tvTime.setText("Time not recorded");
            }
        }
    }
}
