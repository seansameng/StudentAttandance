package com.example.studentattandance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattandance.R;
import com.example.studentattandance.database.entities.AttendanceEntity;

import java.util.List;

public class SimpleAttendanceAdapter extends RecyclerView.Adapter<SimpleAttendanceAdapter.AttendanceViewHolder> {
    
    private List<AttendanceEntity> attendanceList;
    private OnAttendanceChangeListener listener;
    
    public interface OnAttendanceChangeListener {
        void onAttendanceChanged(int position, String status);
    }
    
    public SimpleAttendanceAdapter(List<AttendanceEntity> attendanceList, OnAttendanceChangeListener listener) {
        this.attendanceList = attendanceList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_attendance, parent, false);
        return new SimpleAttendanceAdapter.AttendanceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceEntity attendance = attendanceList.get(position);
        holder.bind(attendance, position);
    }
    
    @Override
    public int getItemCount() {
        return attendanceList.size();
    }
    
    public void updateData(List<AttendanceEntity> newAttendanceList) {
        this.attendanceList = newAttendanceList;
        notifyDataSetChanged();
    }
    
    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName, tvStudentId;
        private RadioGroup radioGroupStatus;
        private RadioButton rbPresent, rbAbsent, rbLate;
        
        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            radioGroupStatus = itemView.findViewById(R.id.radio_group_status);
            rbPresent = itemView.findViewById(R.id.rb_present);
            rbAbsent = itemView.findViewById(R.id.rb_absent);
            rbLate = itemView.findViewById(R.id.rb_late);
        }
        
        public void bind(AttendanceEntity attendance, int position) {
            tvStudentName.setText(attendance.getClassName()); // Use className as display name
            tvStudentId.setText("ID: " + attendance.getStudentId());
            
            // Set initial status
            String status = attendance.getStatus();
            if ("PRESENT".equals(status)) {
                rbPresent.setChecked(true);
            } else if ("ABSENT".equals(status)) {
                rbAbsent.setChecked(true);
            } else if ("LATE".equals(status)) {
                rbLate.setChecked(true);
            } else {
                rbPresent.setChecked(true);
            }
            
            // Set status change listener
            radioGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
                String newStatus = "PRESENT";
                if (checkedId == R.id.rb_absent) {
                    newStatus = "ABSENT";
                } else if (checkedId == R.id.rb_late) {
                    newStatus = "LATE";
                }
                
                if (listener != null) {
                    listener.onAttendanceChanged(position, newStatus);
                }
            });
        }
    }
}
