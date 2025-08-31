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

public class AttendanceMarkingAdapter extends RecyclerView.Adapter<AttendanceMarkingAdapter.AttendanceViewHolder> {
    
    private List<UserEntity> students;
    private List<AttendanceEntity> attendanceRecords;
    private AttendanceChangeListener listener;
    
    public interface AttendanceChangeListener {
        void onAttendanceChanged(String studentId, String status);
    }
    
    public AttendanceMarkingAdapter(List<UserEntity> students, List<AttendanceEntity> attendanceRecords, AttendanceChangeListener listener) {
        this.students = students;
        this.attendanceRecords = attendanceRecords;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_marking, parent, false);
        return new AttendanceViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        UserEntity student = students.get(position);
        holder.bind(student, position);
    }
    
    @Override
    public int getItemCount() {
        return students.size();
    }
    
    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStudentName, tvStudentEmail;
        private RadioGroup radioGroupAttendance;
        private RadioButton rbPresent, rbAbsent, rbLate;
        
        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentEmail = itemView.findViewById(R.id.tv_student_email);
            radioGroupAttendance = itemView.findViewById(R.id.radio_group_attendance);
            rbPresent = itemView.findViewById(R.id.rb_present);
            rbAbsent = itemView.findViewById(R.id.rb_absent);
            rbLate = itemView.findViewById(R.id.rb_late);
        }
        
        public void bind(UserEntity student, int position) {
            tvStudentName.setText(student.getUsername());
            tvStudentEmail.setText(student.getEmail());
            
            // Set current attendance status
            String currentStatus = getCurrentAttendanceStatus(student.getId());
            setAttendanceStatus(currentStatus);
            
            // Setup radio button listener
            radioGroupAttendance.setOnCheckedChangeListener((group, checkedId) -> {
                String status = "";
                if (checkedId == R.id.rb_present) {
                    status = "PRESENT";
                } else if (checkedId == R.id.rb_absent) {
                    status = "ABSENT";
                } else if (checkedId == R.id.rb_late) {
                    status = "LATE";
                }
                
                if (listener != null && !status.isEmpty()) {
                    listener.onAttendanceChanged(student.getId(), status);
                }
            });
        }
        
        private String getCurrentAttendanceStatus(String studentId) {
            for (AttendanceEntity record : attendanceRecords) {
                if (record.getStudentId().equals(studentId)) {
                    return record.getStatus();
                }
            }
            return "PRESENT"; // Default to present
        }
        
        private void setAttendanceStatus(String status) {
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
        }
    }
}
