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
import com.example.studentattandance.database.entities.ClassEnrollmentEntity;
import com.example.studentattandance.database.entities.UserEntity;
import com.example.studentattandance.database.entities.AttendanceEntity;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    
    private List<UserEntity> studentList;
    private List<AttendanceEntity> attendanceList;
    private OnAttendanceChangeListener listener;
    
    public interface OnAttendanceChangeListener {
        void onAttendanceChanged(int position, String status);
    }
    
    public AttendanceAdapter(List<UserEntity> studentList, List<AttendanceEntity> attendanceList,
                           OnAttendanceChangeListener listener) {
        this.studentList = studentList;
        this.attendanceList = attendanceList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        UserEntity student = studentList.get(position);
        AttendanceEntity attendance = attendanceList.get(position);
        holder.bind(student, attendance, position);
    }
    
    @Override
    public int getItemCount() {
        return studentList.size();
    }
    
    public void updateData(List<UserEntity> newStudentList, List<AttendanceEntity> newAttendanceList) {
        this.studentList = newStudentList;
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
        
        public void bind(UserEntity student, AttendanceEntity attendance, int position) {
            tvStudentName.setText(student.getFirstName() + " " + student.getLastName());
            tvStudentId.setText("ID: " + student.getId());
            
            // Set initial status
            String status = attendance.getStatus();
            switch (status) {
                case "PRESENT":
                    rbPresent.setChecked(true);
                    break;
                case "ABSENT":
                    rbAbsent.setChecked(true);
                    break;
                case "LATE":
                    rbLate.setChecked(true);
                    break;
                default:
                    rbPresent.setChecked(true);
                    break;
            }
            
            // Set status change listener
            radioGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
                String newStatus = "";
                if (checkedId == R.id.rb_present) {
                    newStatus = "PRESENT";
                } else if (checkedId == R.id.rb_absent) {
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
